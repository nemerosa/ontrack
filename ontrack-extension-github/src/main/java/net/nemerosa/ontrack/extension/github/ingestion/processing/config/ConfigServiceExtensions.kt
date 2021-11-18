package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.common.BaseException

class GitHubIngestionStoredConfigNotFoundException(owner: String, name: String, branch: String) : BaseException(
    "Cannot find a stored GitHub ingestion configuration for $owner/$name@$branch"
)
