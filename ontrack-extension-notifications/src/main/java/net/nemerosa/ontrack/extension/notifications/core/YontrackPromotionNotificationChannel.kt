package net.nemerosa.ontrack.extension.notifications.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.common.parseDuration
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordFilter
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionConfigException
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionFilter
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.json.*
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.jvm.optionals.getOrNull

@Component
@APIDescription("Promotes a build in Yontrack")
@Documentation(YontrackPromotionNotificationChannelConfig::class)
@Documentation(YontrackPromotionNotificationChannelOutput::class, section = "output")
class YontrackPromotionNotificationChannel(
    private val eventTemplatingService: EventTemplatingService,
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val yontrackBuildNotificationHelper: YontrackBuildNotificationHelper,
    private val notificationRecordingService: NotificationRecordingService,
    private val eventSubscriptionService: EventSubscriptionService,
) : AbstractNotificationChannel<YontrackPromotionNotificationChannelConfig, YontrackPromotionNotificationChannelOutput>(
    YontrackPromotionNotificationChannelConfig::class
) {

    private val logger: Logger = LoggerFactory.getLogger(YontrackPromotionNotificationChannel::class.java)

    override fun validateParsedConfig(config: YontrackPromotionNotificationChannelConfig) {
        if (config.promotion.isBlank()) {
            throw EventSubscriptionConfigException("Promotion level name cannot be blank.")
        }
    }

    override fun mergeConfig(
        a: YontrackPromotionNotificationChannelConfig,
        changes: JsonNode
    ): YontrackPromotionNotificationChannelConfig {
        return YontrackPromotionNotificationChannelConfig(
            project = patchNullableString(changes, a::project),
            branch = patchNullableString(changes, a::branch),
            build = patchNullableString(changes, a::build),
            promotion = patchString(changes, a::promotion),
            waitForPromotion = patchBoolean(changes, a::waitForPromotion),
            waitForPromotionTimeout = if (changes.has(a::waitForPromotionTimeout.name)) {
                val durationValue = changes.get(a::waitForPromotionTimeout.name).asText()
                parseDuration(durationValue) ?: error("Cannot parse duration: $durationValue")
            } else {
                a.waitForPromotionTimeout
            },
        )
    }

    override fun publish(
        recordId: String,
        config: YontrackPromotionNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?,
        outputProgressCallback: (current: YontrackPromotionNotificationChannelOutput) -> YontrackPromotionNotificationChannelOutput
    ): NotificationResult<YontrackPromotionNotificationChannelOutput> {

        val build = securityService.asAdmin { getTargetBuild(config, event, context) }
        val description = eventTemplatingService.renderEvent(event, context, template)

        val promotionLevel = structureService.findPromotionLevelByName(
            project = build.branch.project.name,
            branch = build.branch.name,
            promotionLevel = config.promotion,
        ).getOrNull() ?: return NotificationResult.error("Promotion level not found: ${config.promotion}")

        logger.info("Promoting build ${build.name} to ${promotionLevel.name}...")

        val run = securityService.asAdmin {
            structureService.newPromotionRun(
                PromotionRun.of(
                    build = build,
                    promotionLevel = promotionLevel,
                    signature = securityService.currentSignature,
                    description = description,
                )
            )
        }

        logger.info("Promoted build ${build.name} to ${promotionLevel.name}: ${run.id}")

        val output = YontrackPromotionNotificationChannelOutput(
            runId = run.id(),
            promotionLevelId = promotionLevel.id(),
        )

        return if (config.waitForPromotion) {
            // Promotion created, waiting for the promotion notifications to be completed
            // will be done asynchronously
            NotificationResult.async(
                output
            )
        } else {
            // Done & done
            NotificationResult.ok(
                output
            )
        }


    }

    override fun getNotificationResult(notificationRecord: NotificationRecord): NotificationResult<YontrackPromotionNotificationChannelOutput>? {
        val config = notificationRecord.channelConfig.parse<YontrackPromotionNotificationChannelConfig>()
        val output = notificationRecord.result.output
            ?.parse<YontrackPromotionNotificationChannelOutput>()
        val runId = output
            ?.runId
            ?: return null
        val promotionLevelId = output.promotionLevelId

        return if (config.waitForPromotion) {

            val run = structureService.getPromotionRun(ID.of(runId))
            val runStart = run.signature.time
            val elapsedTime = Duration.between(runStart, Time.now)

            if (elapsedTime > config.waitForPromotionTimeout) {
                return NotificationResult.error(
                    "Timeout after ${config.waitForPromotionTimeout}",
                    output
                )
            }

            val subscriptionsCount = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(
                    entity = ProjectEntityID(
                        type = ProjectEntityType.PROMOTION_LEVEL,
                        id = promotionLevelId
                    )
                )
            ).pageInfo.totalSize
            logger.info("Waiting for $subscriptionsCount subscriptions to complete...")
            if (subscriptionsCount > 0) {
                val records = notificationRecordingService.filter(
                    filter = NotificationRecordFilter(
                        eventEntityId = ProjectEntityID(
                            type = ProjectEntityType.PROMOTION_RUN,
                            id = runId
                        )
                    )
                ).pageItems.filter { it.id != notificationRecord.id }
                if (records.size == subscriptionsCount && records.all { it.result.type == NotificationResultType.OK }) {
                    NotificationResult.ok(output)
                } else if (records.all { it.result.type.running }) {
                    NotificationResult.ongoing(output)
                } else {
                    NotificationResult.error(
                        "At least one notification linked to the promotion failed, was not valid or was interrupted",
                        output
                    )
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    internal fun getTargetBuild(
        config: YontrackPromotionNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>
    ): Build {
        val buildName = config.build?.run { eventTemplatingService.renderEvent(event, context, this) }
        val branchName = config.branch?.run { eventTemplatingService.renderEvent(event, context, this) }
        val projectName = config.project?.run { eventTemplatingService.renderEvent(event, context, this) }
        return yontrackBuildNotificationHelper.getBuild(
            event = event,
            projectName = projectName,
            branchName = branchName,
            buildName = buildName,
        )
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            YontrackPromotionNotificationChannelConfig::promotion.name to text
        ).asJson()

    override val type: String = "yontrack-promotion"
    override val displayName: String = "Yontrack promotion"
    override val enabled: Boolean = true
}