package net.nemerosa.ontrack.extension.environments

data class SlotPipelineDeploymentStatusProgress(
    val ok: Int,
    val overridden: Boolean,
    val total: Int,
) {
    val percentage: Int = if (total > 0) ok * 100 / total else 100
}
