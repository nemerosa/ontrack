package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration of the post-processing for GitHub.
 */
data class GitHubPostProcessingConfig(
    @APIDescription("This image defines the environment for the upgrade command to run in")
    val dockerImage: String,
    @APIDescription("Command to run in the Docker container")
    val dockerCommand: String,
    @APIDescription("Commit message to use to commit and push the result of the post-processing")
    val commitMessage: String,
    @APIDescription("GitHub configuration to use for the connection (optional, using defaults if not specified)")
    val config: String?,
    @APIDescription("GitHub repository (`owner/repo`). To be set to override the default settings.")
    val repository: String?,
    @APIDescription("If defined, name of the workflow in _this_ repository containing the post-processing (like `post-processing.yml`)")
    val workflow: String?,
    @APIDescription("If defined, overrides the default settings for the branch to use when launching the workflow")
    val branch: String?,
    @APIDescription("List of extra parameters to pass to the workflow (optional, none by default)")
    @ListRef
    val parameters: List<GitHubPostProcessingConfigParam> = emptyList(),
)
