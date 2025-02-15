package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingCommitsOption
import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingServiceConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

class ChangelogDeploymentTemplatingContextConfig(
    @APIDescription("Status to use when looking for the previous deployment")
    val since: SlotPipelineStatus = SlotPipelineStatus.DONE,
    empty: String = "",
    dependencies: List<String> = emptyList(),
    title: Boolean = false,
    allQualifiers: Boolean = false,
    defaultQualifierFallback: Boolean = false,
    commitsOption: ChangeLogTemplatingCommitsOption = ChangeLogTemplatingCommitsOption.NONE,
) : ChangeLogTemplatingServiceConfig(
    empty = empty,
    dependencies = dependencies,
    title = title,
    allQualifiers = allQualifiers,
    defaultQualifierFallback = defaultQualifierFallback,
    commitsOption = commitsOption
)
