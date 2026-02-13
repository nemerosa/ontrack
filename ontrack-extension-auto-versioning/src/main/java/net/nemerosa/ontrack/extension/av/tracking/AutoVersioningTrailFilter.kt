package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.common.api.APIDescription

data class AutoVersioningTrailFilter(
    val onlyEligible: Boolean = true,
    @APIDescription("Part of the project name")
    val projectName: String? = null,
) {
    fun filter(branches: List<AutoVersioningBranchTrail>) =
        branches
            .filter {
                this.projectName.isNullOrBlank() || it.branch.project.name.contains(this.projectName, ignoreCase = true)
            }
            .filter {
                !onlyEligible || it.isEligible()
            }

    companion object {
        val all = AutoVersioningTrailFilter(
            onlyEligible = false,
        )
    }
}
