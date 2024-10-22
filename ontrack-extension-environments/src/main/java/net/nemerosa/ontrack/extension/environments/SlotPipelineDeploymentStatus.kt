package net.nemerosa.ontrack.extension.environments

data class SlotPipelineDeploymentStatus(
    val checks: List<SlotPipelineDeploymentCheck>,
) {
    val status: Boolean = checks.all { it.check.status || it.override != null }
    val override: Boolean = checks.any { it.override != null }
}
