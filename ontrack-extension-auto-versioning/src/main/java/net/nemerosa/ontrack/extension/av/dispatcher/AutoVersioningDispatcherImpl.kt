package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.model.PromotionEvent
import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueue
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class AutoVersioningDispatcherImpl(
    private val securityService: SecurityService,
    private val queue: AutoVersioningQueue,
    private val versionSourceFactory: VersionSourceFactory,
    private val applicationLogService: ApplicationLogService,
) : AutoVersioningDispatcher {

    override fun dispatch(configuredBranches: AutoVersioningConfiguredBranches) {
        securityService.asAdmin {
            configuredBranches.configuredBranches.forEach { configuredBranch ->
                val branch = configuredBranch.branch
                configuredBranch.configurations.forEach { config ->
                    val order = createAutoVersioningOrder(configuredBranches.promotionRun, branch, config)
                    // Posts the event on the queue
                    if (order != null) {
                        queue.queue(order)
                    }
                }
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
                targetPaths = config.getTargetPaths(),
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
            )
        } catch (ex: Exception) {
            // Logging the event
            applicationLogService.log(
                ApplicationLogEntry.error(
                    ex,
                    NameDescription.nd("auto-versioning-version-error", "Auto versioning version error"),
                    "Build ${promotionRun.build.id} (${promotionRun.build.entityDisplayName}) was promoted, " +
                            "but is not eligible to auto versioning because no version was returned or there " +
                            "was an error. "
                )
                    .withDetail("auto-versioning-source-build", promotionRun.build.entityDisplayName)
                    .withDetail("auto-versioning-source-promotion", promotionRun.promotionLevel.name)
                    .withDetail("auto-versioning-target-branch", branch.entityDisplayName)
            )
            // Not going further with the auto versioning request
            return null
        }
    }

    private fun getBuildSourceVersion(promotionRun: PromotionRun, config: AutoVersioningSourceConfig): String {
        return versionSourceFactory.getBuildVersion(promotionRun.build, config)
    }
}