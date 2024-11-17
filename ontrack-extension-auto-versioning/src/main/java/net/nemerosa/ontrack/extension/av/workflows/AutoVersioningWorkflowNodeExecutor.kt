package net.nemerosa.ontrack.extension.av.workflows

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.AbstractTypedWorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import org.springframework.stereotype.Component
import java.util.*

@Component
class AutoVersioningWorkflowNodeExecutor(
    extensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningProcessingService: AutoVersioningProcessingService,
) : AbstractTypedWorkflowNodeExecutor<AutoVersioningWorkflowNodeExecutorData>(
    feature = extensionFeature,
    id = "auto-versioning",
    displayName = "Auto-versioning of a given branch",
    dataType = AutoVersioningWorkflowNodeExecutorData::class,
) {

    override fun execute(
        workflowInstance: WorkflowInstance,
        data: AutoVersioningWorkflowNodeExecutorData,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit
    ): WorkflowNodeExecutorResult {
        val order = createOrder(workflowInstance, data)
        val outcome = autoVersioningProcessingService.process(order)
        TODO("Checks the outcome")
    }

    private fun createOrder(
        workflowInstance: WorkflowInstance,
        data: AutoVersioningWorkflowNodeExecutorData,
    ): AutoVersioningOrder {
        return AutoVersioningOrder(
            uuid = UUID.randomUUID().toString(),
            sourceProject = TODO(),
            sourceBuildId = null,
            sourcePromotionRunId = null,
            sourcePromotion = null,
            sourceBackValidation = null,
            branch = TODO(),
            targetPath = data.targetPath,
            targetRegex = data.targetRegex,
            targetProperty = data.targetProperty,
            targetPropertyRegex = data.targetPropertyRegex,
            targetPropertyType = data.targetPropertyType,
            targetVersion = data.targetVersion,
            autoApproval = data.autoApproval,
            upgradeBranchPattern = data.upgradeBranchPattern,
            postProcessing = data.postProcessing,
            postProcessingConfig = data.postProcessingConfig,
            validationStamp = data.validationStamp,
            autoApprovalMode = data.autoApprovalMode,
            reviewers = data.reviewers,
            prTitleTemplate = data.prTitleTemplate,
            prBodyTemplate = data.prBodyTemplate,
            prBodyTemplateFormat = data.prBodyTemplateFormat,
            additionalPaths = data.additionalPaths,
        )
    }
}
