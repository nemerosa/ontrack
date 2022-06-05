package net.nemerosa.ontrack.extension.jenkins.autoversioning

/**
 * Configuration for the [JenkinsPostProcessing] service.
 *
 * @property dockerImage Docker image defining the environment
 * @property dockerCommand Command to run in the working copy inside the Docker container
 * @property commitMessage Commit message for the post processed files. If not defined, a default message will be provide
 * @property config Jenkins configuration to use for the connection (optional, using defaults if not specified)
 * @property job Path to the job to launch for the post processing (optional, using defaults if not specified)
 * @property credentials List of credentials to inject in the command
 */
class JenkinsPostProcessingConfig(
        val dockerImage: String,
        val dockerCommand: String,
        val commitMessage: String?,
        val config: String?,
        val job: String?,
        val credentials: List<JenkinsPostProcessingConfigCredentials>
)

