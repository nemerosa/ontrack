package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

/**
 * Requires an ingestion configuration for a repository and a branch
 */
fun ConfigService.getConfig(repository: Repository, branch: String): IngestionConfig =
    findConfig(repository, branch)
        ?: throw GitHubIngestionStoredConfigNotFoundException(repository.owner.login, repository.name, branch)

class GitHubIngestionStoredConfigNotFoundException(owner: String, name: String, branch: String) : BaseException(
    "Cannot find a stored GitHub ingestion configuration for $owner/$name@branch"
)
