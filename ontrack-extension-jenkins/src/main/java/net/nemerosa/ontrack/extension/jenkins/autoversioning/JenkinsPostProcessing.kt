package net.nemerosa.ontrack.extension.jenkins.autoversioning

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessing
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingMissingConfigException
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.JenkinsExtensionFeature
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

/**
 * Upgrade post processing based on a generic job in Jenkins.
 */
@Component
class JenkinsPostProcessing(
    extensionFeature: JenkinsExtensionFeature,
    private val cachedSettingsService: CachedSettingsService,
    private val jenkinsConfigurationService: JenkinsConfigurationService,
    private val jenkinsClientFactory: JenkinsClientFactory,
    // private val autoVersioningNotificationService: AutoVersioningNotificationService
) : AbstractExtension(extensionFeature), PostProcessing<JenkinsPostProcessingConfig> {

    override val id: String = "jenkins"

    override val name: String = "Jenkins job post processing"

    override fun parseAndValidate(config: JsonNode?): JenkinsPostProcessingConfig {
        return if (config != null && !config.isNull) {
            // Parsing
            config.parse()
        } else {
            throw PostProcessingMissingConfigException()
        }
    }

    override fun postProcessing(
        config: JenkinsPostProcessingConfig,
        autoVersioningOrder: AutoVersioningOrder,
        repositoryURI: String,
        repository: String,
        upgradeBranch: String,
    ) {
        // Gets the global settings
        val settings: JenkinsPostProcessingSettings =
            cachedSettingsService.getCachedSettings(JenkinsPostProcessingSettings::class.java)
        // Configuration
        val jenkinsConfigName = config.config ?: settings.config
        val jenkinsJobPath = config.job ?: settings.job
        // Checks the configuration
        if (jenkinsConfigName.isBlank() || jenkinsJobPath.isBlank()) {
            throw JenkinsPostProcessingSettingsNotFoundException()
        }
        // Gets the Jenkins configuration by name
        val jenkinsConfig: JenkinsConfiguration = jenkinsConfigurationService.getConfiguration(jenkinsConfigName)

        // Gets a Jenkins client for this configuration
        val jenkinsClient = jenkinsClientFactory.getClient(jenkinsConfig)

        // Launches the job and waits for its completion
        try {
            val jenkinsBuild = jenkinsClient.runJob(
                jenkinsJobPath,
                mapOf(
                    "DOCKER_IMAGE" to config.dockerImage,
                    "DOCKER_COMMAND" to config.dockerCommand,
                    "COMMIT_MESSAGE" to (config.commitMessage ?: autoVersioningOrder.defaultCommitMessage),
                    "REPOSITORY_URI" to repositoryURI,
                    "UPGRADE_BRANCH" to upgradeBranch,
                    "CREDENTIALS" to (config.credentials?.renderParameter() ?: ""),
                ),
                settings.retries,
                settings.retriesDelaySeconds
            )

            // Check for success
            if (!jenkinsBuild.successful) {
                throw JenkinsPostProcessingJobFailureException(
                    jenkins = jenkinsConfig.url,
                    job = jenkinsJobPath,
                    build = jenkinsBuild.id,
                    buildUrl = jenkinsBuild.url,
                    result = jenkinsBuild.result
                )
            }
        } catch (e: Exception) {
            if (e !is JenkinsPostProcessingJobFailureException) {
                // Feedback already provided from inside the jenkins build
                // TODO autoVersioningNotificationService.sendErrorNotification(prCreationOrder, "Failed to create post-processing build for ontrack auto-upgrade: ${e.message}")
            }
            throw e
        }
    }

    private val AutoVersioningOrder.defaultCommitMessage: String
        get() = "Post processing for version change in $targetPaths for $targetVersion"

}
