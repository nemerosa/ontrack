package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Input for the links for a build identified by its name
 */
@APIDescription("Input for the links for a build identified by its name")
class GitHubIngestionLinksByBuildNameInput(
    owner: String,
    repository: String,
    buildLinks: List<GitHubIngestionLink>,
    addOnly: Boolean,
    @APIDescription("Name of the build")
    val buildName: String,
) : AbstractGitHubIngestionLinksInput(
    owner,
    repository,
    buildLinks,
    addOnly,
) {
    override fun toPayload() = GitHubIngestionLinksPayload(
        owner = owner,
        repository = repository,
        buildLinks = buildLinks,
        buildName = buildName,
        addOnly = addOnly ?: GitHubIngestionLinksPayload.ADD_ONLY_DEFAULT,
    )
}