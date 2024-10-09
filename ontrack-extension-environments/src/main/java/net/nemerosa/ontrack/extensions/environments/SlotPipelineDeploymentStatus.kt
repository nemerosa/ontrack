package net.nemerosa.ontrack.extensions.environments

data class SlotPipelineDeploymentStatus(
    val checks: List<SlotPipelineDeploymentCheck>,
) {
    val status: Boolean = checks.all { it.status }
}
