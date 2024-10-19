package net.nemerosa.ontrack.extensions.environments

data class SlotPipelineDeploymentFinishStatus(
    val deployed: Boolean,
    val message: String,
) {
    companion object {
        fun ok(message: String) = SlotPipelineDeploymentFinishStatus(
            deployed = true,
            message = message,
        )
        fun nok(message: String) = SlotPipelineDeploymentFinishStatus(
            deployed = false,
            message = message,
        )
    }
}
