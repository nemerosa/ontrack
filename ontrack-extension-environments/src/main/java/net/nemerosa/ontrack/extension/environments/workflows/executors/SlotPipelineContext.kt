package net.nemerosa.ontrack.extension.environments.workflows.executors

data class SlotPipelineContext(
    val pipelineId: String,
) {
    companion object {
        val CONTEXT: String = SlotPipelineContext::class.java.simpleName
    }
}