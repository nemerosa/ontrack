package net.nemerosa.ontrack.extension.environments

data class SlotPipelineDeploymentStatus(
    val checks: List<SlotPipelineDeploymentCheck>,
) {
    val status: Boolean = checks.all { it.check.status || it.override != null }
    val override: Boolean = checks.any { it.override != null }

    val progress: SlotPipelineDeploymentStatusProgress = SlotPipelineDeploymentStatusProgress(
        ok = checks.count { it.check.status || it.override != null },
        overridden = override,
        total = checks.size,
    )
}
