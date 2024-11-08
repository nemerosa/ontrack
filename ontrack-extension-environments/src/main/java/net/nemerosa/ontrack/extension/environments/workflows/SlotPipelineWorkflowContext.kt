package net.nemerosa.ontrack.extension.environments.workflows

data class SlotPipelineWorkflowContext(
    val pipelineId: String,
    val slotWorkflowId: String,
) {
    companion object {
        val CONTEXT: String = SlotPipelineWorkflowContext::class.java.simpleName
    }
}