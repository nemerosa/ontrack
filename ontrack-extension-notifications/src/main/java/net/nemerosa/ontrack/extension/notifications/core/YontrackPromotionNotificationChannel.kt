package net.nemerosa.ontrack.extension.notifications.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.common.parseDuration
import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordFilter
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionConfigException
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionFilter
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.patchBoolean
import net.nemerosa.ontrack.json.patchNullableString
import net.nemerosa.ontrack.json.patchString
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.toProjectEntityID
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull
import kotlin.time.toKotlinDuration

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

        // Waiting for the promotion notifications to be completed & successful
        if (config.waitForPromotion) {
            val subscriptionsCount = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(
                    entity = promotionLevel.toProjectEntityID(),
                )
            ).pageInfo.totalSize
            if (subscriptionsCount > 0) {
                waitForPromotion(recordId, subscriptionsCount, run, config)
            }
        }

        return NotificationResult.ok(
            YontrackPromotionNotificationChannelOutput(
                runId = run.id(),
            )
        )
    }

    private fun waitForPromotion(
        recordId: String,
        subscriptionsCount: Int,
        run: PromotionRun,
        config: YontrackPromotionNotificationChannelConfig
    ) {
        waitFor(
            message = "Waiting for promotion notifications to complete: $run",
            timeout = config.waitForPromotionTimeout.toKotlinDuration(),
        ) {
            notificationRecordingService.filter(
                filter = NotificationRecordFilter(
                    eventEntityId = run.toProjectEntityID()
                )
            ).pageItems.filter { it.id != recordId }
        } until { records ->
            records.size == subscriptionsCount && records.all { it.result.type == NotificationResultType.OK }
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