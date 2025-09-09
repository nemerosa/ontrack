package net.nemerosa.ontrack.extension.github.autoversioning

/**
 * Configuration of the post-processing for GitHub.
 *
 * @property dockerImage This image defines the environment for the upgrade command to run in
 * @property dockerCommand Command to run in the Docker container
 * @property commitMessage Commit message to use to commit and push the result of the post-processing
 * @property config GitHub configuration to use for the connection (optional, using defaults if not specified)
 * @property workflow If defined, name of the workflow in _this_ repository containing the post-processing (like `post-processing.yml`)
 * @property parameters List of extra parameters to pass to the workflow (optional, none by default)
 */
data class GitHubPostProcessingConfig(
    val dockerImage: String,
    val dockerCommand: String,
    val commitMessage: String,
    val config: String?,
    val workflow: String?,
    val parameters: List<GitHubPostProcessingConfigParam> = emptyList(),
)
