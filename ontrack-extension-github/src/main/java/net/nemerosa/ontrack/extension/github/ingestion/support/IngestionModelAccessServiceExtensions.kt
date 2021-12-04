package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

fun IngestionModelAccessService.getOrCreateBranch(
    repository: Repository,
    configuration: String?,
    headBranch: String,
    pullRequest: PullRequest?,
) = getOrCreateBranch(
    project = getOrCreateProject(repository, configuration),
    headBranch = headBranch,
    pullRequest = pullRequest,
)
