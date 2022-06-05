package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.model.PromotionEvent
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component
import java.util.*

@Component
class AutoVersioningDispatcherImpl(
    private val securityService: SecurityService,
) : AutoVersioningDispatcher {
    override fun dispatch(configuredBranches: AutoVersioningConfiguredBranches) {
        securityService.asAdmin {
            configuredBranches.configuredBranches.forEach { configuredBranch ->
                val branch = configuredBranch.branch
                configuredBranch.configurations.forEach { config ->
                    val order = createAutoVersioningOrder(configuredBranches.promotionEvent, branch, config)
                    // Posts the event on the queue
                    // TODO orderService.postPRCreationOrder(order)
                    // Audit
                    // TODO autoVersioningAuditService.onQueuing(order)
                }
            }
        }
    }

    private fun createAutoVersioningOrder(
        event: PromotionEvent,
        branch: Branch,
        config: AutoVersioningSourceConfig,
    ) = AutoVersioningOrder(
        uuid = UUID.randomUUID().toString(),
        sourceProject = event.build.project.name,
        branch = branch,
        targetPaths = config.getTargetPaths(),
        targetRegex = config.targetRegex,
        targetProperty = config.targetProperty,
        targetPropertyRegex = config.targetPropertyRegex,
        targetPropertyType = config.targetPropertyType,
        targetVersion = event.version,
        autoApproval = config.autoApproval ?: true,
        upgradeBranchPattern = config.upgradeBranchPattern ?: AutoVersioningSourceConfig.DEFAULT_UPGRADE_BRANCH_PATTERN,
        postProcessing = config.postProcessing,
        postProcessingConfig = config.postProcessingConfig,
        validationStamp = config.validationStamp,
        autoApprovalMode = config.autoApprovalMode ?: AutoVersioningSourceConfig.DEFAULT_AUTO_APPROVAL_MODE,
    )
}