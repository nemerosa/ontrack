package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Link to a build")
data class GitHubIngestionLink(
    @APIDescription("Name of the project to link to")
    val project: String,
    @APIDescription("Name or label of the build to link to (labels start with #)")
    val buildRef: String,
)