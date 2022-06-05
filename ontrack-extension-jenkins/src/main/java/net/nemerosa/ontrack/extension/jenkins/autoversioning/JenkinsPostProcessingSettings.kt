package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Configuration of the Jenkins post-processing.
 *
 * @property config Default Jenkins configuration to use for the connection
 * @property job Default path to the job to launch for the post processing
 * @property retries The amount of times we check for successful scheduling and completion of the post-processing job
 * @property retriesDelaySeconds The time (in seconds) between two checks for successful scheduling and completion of
 *                               the post-processing job
 */
class JenkinsPostProcessingSettings(
    @APILabel("Configuration")
    @APIDescription("Default Jenkins configuration to use for the connection")
    val config: String,
    @APILabel("Job")
    @APIDescription("Default path to the job to launch for the post processing")
    val job: String,
    @APILabel("Retries")
    @APIDescription("The amount of times we check for successful scheduling and completion of the post-processing job")
    val retries: Int,
    @APILabel("Retry internal")
    @APIDescription("The time (in seconds) between two checks for successful scheduling and completion of the post-processing job")
    val retriesDelaySeconds: Int,
) {
    companion object {
        const val DEFAULT_RETRIES = 10
        const val DEFAULT_RETRIES_DELAY_SECONDS = 30
    }
}
