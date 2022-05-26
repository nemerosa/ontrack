package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Common data between all the inputs
 */
abstract class AbstractGitHubIngestionLinksInput(
    @APIDescription("Name of the repository owner to target")
    val owner: String,
    @APIDescription("Name of the repository to target")
    val repository: String,
    @APIDescription("List of links")
    @ListRef(embedded = true)
    val buildLinks: List<GitHubIngestionLink>,
) {
    abstract fun toPayload(): GitHubIngestionLinksPayload
}
