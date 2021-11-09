package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

fun IngestionModelAccessService.getOrCreateBranch(
    repository: Repository,
    headBranch: String,
    baseBranch: String?,
) = getOrCreateBranch(
    project = getOrCreateProject(repository),
    headBranch = headBranch,
    baseBranch = baseBranch
)
