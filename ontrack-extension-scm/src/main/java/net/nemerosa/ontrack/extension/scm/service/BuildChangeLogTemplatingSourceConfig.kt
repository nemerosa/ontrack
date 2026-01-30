package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingCommitsOption
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig

class BuildChangeLogTemplatingSourceConfig(
    empty: String = "",
    dependencies: List<String> = emptyList(),
    title: Boolean = false,
    allQualifiers: Boolean = false,
    defaultQualifierFallback: Boolean = false,
    commitsOption: ChangeLogTemplatingCommitsOption = ChangeLogTemplatingCommitsOption.NONE,
    @APIDescription("ID to the build to get the change log from")
    val from: Int,
) : ChangeLogTemplatingServiceConfig(
    empty,
    dependencies,
    title,
    allQualifiers,
    defaultQualifierFallback,
    commitsOption,
)