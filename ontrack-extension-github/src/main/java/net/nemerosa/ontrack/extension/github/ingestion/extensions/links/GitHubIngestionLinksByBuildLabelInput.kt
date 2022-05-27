package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Input for the links for a build identified by its label
 */
@APIDescription("Input for the links for a build identified by its label")
class GitHubIngestionLinksByBuildLabelInput(
    owner: String,
    repository: String,
    buildLinks: List<GitHubIngestionLink>,
    @APIDescription("Label of the build")
    val buildLabel: String,
) : AbstractGitHubIngestionLinksInput(
    owner,
    repository,
    buildLinks,
) {
    override fun toPayload() = GitHubIngestionLinksPayload(
        owner = owner,
        repository = repository,
        buildLinks = buildLinks,
        buildLabel = buildLabel,
    )
}