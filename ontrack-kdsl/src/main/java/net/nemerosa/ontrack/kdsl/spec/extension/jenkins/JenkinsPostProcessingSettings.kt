package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

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
    val config: String,
    val job: String,
    val retries: Int = DEFAULT_RETRIES,
    val retriesDelaySeconds: Int = DEFAULT_RETRIES_DELAY_SECONDS,
) {
    companion object {
        const val DEFAULT_RETRIES = 10
        const val DEFAULT_RETRIES_DELAY_SECONDS = 30
    }
}
