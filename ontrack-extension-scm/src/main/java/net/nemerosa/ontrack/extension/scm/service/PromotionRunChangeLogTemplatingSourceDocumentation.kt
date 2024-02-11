package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.annotations.APIDescription

data class PromotionRunChangeLogTemplatingSourceDocumentation(
    @APIDescription("By default, if a previous promotion is not found on the current branch, it'll be looked for in all branches of the projects. Set this parameter to `false` to disable this behaviour.")
    val acrossBranches: Boolean = true,
    @APIDescription("Use a comma-separated list of projects to get a deep changelog.")
    val projects: List<String> = emptyList(),
    @APIDescription("Include a title for the change log")
    val title: Boolean = false,
)
