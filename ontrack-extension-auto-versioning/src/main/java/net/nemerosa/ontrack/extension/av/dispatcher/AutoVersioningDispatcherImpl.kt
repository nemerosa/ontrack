package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueuePayload
import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueueProcessor
import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueueSourceData
import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueueSourceExtension
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTracking
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class AutoVersioningDispatcherImpl(
    private val securityService: SecurityService,
    private val queueDispatcher: QueueDispatcher,
    private val queueProcessor: AutoVersioningQueueProcessor,
    private val queueSourceExtension: AutoVersioningQueueSourceExtension,
    private val versionSourceFactory: VersionSourceFactory,
    private val autoVersioningAuditService: AutoVersioningAuditService,
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val metrics: AutoVersioningMetricsService,
) : AutoVersioningDispatcher {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningDispatcherImpl::class.java)

    override fun dispatch(
        promotionRun: PromotionRun,
        tracking: AutoVersioningTracking,
    ) {
        securityService.asAdmin {
            val trail = tracking.trail
            if (trail != null) {
                trail.branches.forEach { branchTrail ->
                    if (branchTrail.isEligible()) {
                        val order = createAutoVersioningOrder(
                            promotionRun = promotionRun,
                            branch = branchTrail.branch,
                            config = branchTrail.configuration,
                        )
                        // Posts the event on the queue
                        if (order != null) {
                            tracking.withTrail {
                                it.withOrder(branchTrail, order)
                            }

                            // Cancelling any previous order
                            if (autoVersioningConfigProperties.queue.cancelling) {
                                autoVersioningAuditService.cancelQueuedOrders(order)
                            }

                            // Sending the request on the queue
                            val result = queueDispatcher.dispatch(
                                queueProcessor = queueProcessor,
                                payload = AutoVersioningQueuePayload(
                                    order = order,
                                ),
                                source = queueSourceExtension.createQueueSource(
                                    AutoVersioningQueueSourceData(
                                        orderUuid = order.uuid,
                                    )
                                ),
                            )

                            // Audit & metrics
                            val routingKey = result.routingKey ?: "n/a"
                            autoVersioningAuditService.onQueuing(
                                order,
                                routingKey,
                            )
                            metrics.onQueuing(order, routingKey)
                        }
                    }
                }
            } else {
                logger.error("Dispatching not possible because no trail was created (auto-versioning may not be enabled after all)")
            }
        }
    }

    private fun createAutoVersioningOrder(
        promotionRun: PromotionRun,
        branch: Branch,
        config: AutoVersioningSourceConfig,
    ): AutoVersioningOrder? {
        try {
            val version = getBuildSourceVersion(promotionRun, config)
            return AutoVersioningOrder(
                uuid = UUID.randomUUID().toString(),
                sourceProject = promotionRun.build.project.name,
                sourceBuildId = promotionRun.build.id(),
                sourcePromotionRunId = promotionRun.id(),
                sourcePromotion = promotionRun.promotionLevel.name,
                sourceBackValidation = config.backValidation,
                branch = branch,
                targetPath = config.targetPath,
                targetRegex = config.targetRegex,
                targetProperty = config.targetProperty,
                targetPropertyRegex = config.targetPropertyRegex,
                targetPropertyType = config.targetPropertyType,
                targetVersion = version,
                autoApproval = config.autoApproval ?: true,
                upgradeBranchPattern = config.upgradeBranchPattern
                    ?: AutoVersioningSourceConfig.DEFAULT_UPGRADE_BRANCH_PATTERN,
                postProcessing = config.postProcessing,
                postProcessingConfig = config.postProcessingConfig,
                validationStamp = config.validationStamp,
                autoApprovalMode = config.autoApprovalMode ?: AutoVersioningSourceConfig.DEFAULT_AUTO_APPROVAL_MODE,
                reviewers = config.reviewers ?: emptyList(),
                prTitleTemplate = config.prTitleTemplate,
                prBodyTemplate = config.prBodyTemplate,
                prBodyTemplateFormat = config.prBodyTemplateFormat,
                additionalPaths = config.additionalPaths ?: emptyList(),
            )
        } catch (ex: Exception) {
            // Logging the event
            logger.error(
                """
                    Build ${promotionRun.build.id} (${promotionRun.build.entityDisplayName}) was promoted to 
                    ${promotionRun.promotionLevel.name}, but is not eligible to auto-versioning because no 
                    version was returned or there was an error.
                """.trimIndent(),
                ex
            )
            // Not going further with the auto versioning request
            return null
        }
    }

    private fun getBuildSourceVersion(promotionRun: PromotionRun, config: AutoVersioningSourceConfig): String {
        return versionSourceFactory.getBuildVersion(promotionRun.build, config)
    }
}