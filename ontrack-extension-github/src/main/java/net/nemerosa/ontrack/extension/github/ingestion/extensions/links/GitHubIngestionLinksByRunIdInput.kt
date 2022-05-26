package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Input for the links for a build identified by GHA workflow run ID
 */
@APIDescription("Input for the links for a build identified by GHA workflow run ID")
class GitHubIngestionLinksByRunIdInput(
    owner: String,
    repository: String,
    buildLinks: List<GitHubIngestionLink>,
    @APIDescription("ID of the GHA workflow run")
    val runId: Long,
) : AbstractGitHubIngestionLinksInput(
    owner,
    repository,
    buildLinks,
) {
    override fun toPayload() = GitHubIngestionLinksPayload(
        owner = owner,
        repository = repository,
        buildLinks = buildLinks,
        runId = runId,
    )
}