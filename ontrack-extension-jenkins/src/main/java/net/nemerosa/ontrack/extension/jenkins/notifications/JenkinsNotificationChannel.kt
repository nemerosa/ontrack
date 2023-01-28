package net.nemerosa.ontrack.extension.jenkins.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class JenkinsNotificationChannel(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
    private val jenkinsClientFactory: JenkinsClientFactory,
) : AbstractNotificationChannel<JenkinsNotificationChannelConfig>(
    JenkinsNotificationChannelConfig::class
) {
    override fun publish(config: JenkinsNotificationChannelConfig, event: Event): NotificationResult {
        // Gets the Jenkins configuration
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
            ?: return NotificationResult.invalidConfiguration("Jenkins configuration cannot be found: ${config.config}")
        // Gets the Jenkins client
        val jenkinsClient = jenkinsClientFactory.getClient(jenkinsConfig)
        // Running the job
        val parameters = config.parameters.map {
            it.name to it.value
        }.toMap()
        val error = when (config.callMode) {
            JenkinsNotificationChannelConfigCallMode.ASYNC -> launchAsync(jenkinsClient, config, parameters)
            JenkinsNotificationChannelConfigCallMode.SYNC -> launchSync(jenkinsClient, config, parameters)
        }
        // If validation is required
        return if (!config.validation.isNullOrBlank() && !config.validationTarget.isNullOrBlank()) {
            TODO("Validation of the build target")
        }
        // In case of error
        else if (error != null) {
            NotificationResult.error(error)
        }
        // OK
        else {
            NotificationResult.ok()
        }
    }

    private fun launchSync(
        jenkinsClient: JenkinsClient,
        config: JenkinsNotificationChannelConfig,
        parameters: Map<String, String>
    ): String? {
        val interval = 30 // seconds
        val retries = config.timeout / interval
        val build = jenkinsClient.runJob(
            job = config.job,
            parameters = parameters,
            retries = retries,
            retriesDelaySeconds = interval,
        )
        return if (build.successful) {
            null // No error
        } else {
            "Jenkins build at ${build.url} was not reported successful."
        }
    }

    private fun launchAsync(
        jenkinsClient: JenkinsClient,
        config: JenkinsNotificationChannelConfig,
        parameters: Map<String, String>
    ): String? {
        val queueURI = jenkinsClient.fireAndForgetJob(
            job = config.job,
            parameters = parameters,
        )
        return if (queueURI != null) {
            null // No error
        } else {
            "Could not find job at ${config.job}"
        }
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            JenkinsNotificationChannelConfig::config.name to text,
        ).asJson()

    override val type: String = "jenkins"
    override val enabled: Boolean = true

    override fun getForm(c: JenkinsNotificationChannelConfig?): Form {
        TODO("Not yet implemented")
    }

    override fun toText(config: JenkinsNotificationChannelConfig): String {
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
        return if (jenkinsConfig != null) {
            "${jenkinsConfig.url}/${config.job}"
        } else {
            "n/a"
        }
    }

}