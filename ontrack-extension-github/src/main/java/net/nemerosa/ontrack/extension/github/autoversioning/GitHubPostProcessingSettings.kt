package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

class GitHubPostProcessingSettings(
    @APILabel("Configuration")
    @APIDescription("Default GitHub configuration to use for the connection")
    val config: String?,
    @APILabel("Repository")
    @APIDescription("Default repository (like `owner/repository`) containing the workflow to run")
    val repository: String?,
    @APILabel("Workflow")
    @APIDescription("Name of the workflow containing the post-processing (like `post-processing.yml`)")
    val workflow: String?,
    @APILabel("Branch")
    @APIDescription("Branch to launch for the workflow")
    val branch: String = DEFAULT_BRANCH,
    @APILabel("Retries")
    @APIDescription("The amount of times we check for successful scheduling and completion of the post-processing job")
    val retries: Int = DEFAULT_RETRIES,
    @APILabel("Retry interval")
    @APIDescription("The time (in seconds) between two checks for successful scheduling and completion of the post-processing job")
    val retriesDelaySeconds: Int = DEFAULT_RETRIES_DELAY_SECONDS,
) {
    companion object {
        const val DEFAULT_BRANCH = "main"
        const val DEFAULT_RETRIES = 10
        const val DEFAULT_RETRIES_DELAY_SECONDS = 30
    }
}
