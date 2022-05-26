package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractGitHubIngestionBuildPayload

class GitHubIngestionLinksPayload(
    owner: String,
    repository: String,
    runId: Long? = null,
    buildName: String? = null,
    buildLabel: String? = null,
    val buildLinks: List<GitHubIngestionLink>,
) : AbstractGitHubIngestionBuildPayload(
    owner, repository, runId, buildName, buildLabel
)
