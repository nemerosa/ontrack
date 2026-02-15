package net.nemerosa.ontrack.extension.av.ci

import net.nemerosa.ontrack.common.api.APIDescription

data class AutoVersioningBranchCIConfigBranchFilter(
    @APIDescription("List of branch names to include using regular expressions. If empty, all branches are included.")
    val includes: List<String> = listOf(".*"),
    @APIDescription("List of branch names to exclude using regular expressions. If empty, no branch is excluded.")
    val excludes: List<String> = emptyList(),
)
