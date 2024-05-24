package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.annotations.APIDescription

open class PromotionChangeLogTemplatingServiceConfig(
    empty: String = "",
    dependencies: List<String> = emptyList(),
    title: Boolean = false,
    allQualifiers: Boolean = false,
    defaultQualifierFallback: Boolean = false,
    commitsOption: ChangeLogTemplatingCommitsOption = ChangeLogTemplatingCommitsOption.NONE,
    @APIDescription("By default, if a previous promotion is not found on the current branch, it'll be looked for in all branches of the projects. Set this parameter to `false` to disable this behaviour.")
    val acrossBranches: Boolean = true,
) : ChangeLogTemplatingServiceConfig(
    empty = empty,
    dependencies = dependencies,
    title = title,
    allQualifiers = allQualifiers,
    defaultQualifierFallback = defaultQualifierFallback,
    commitsOption = commitsOption,
)