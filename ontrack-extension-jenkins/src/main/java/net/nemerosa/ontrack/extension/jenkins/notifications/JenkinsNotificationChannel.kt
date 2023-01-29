package net.nemerosa.ontrack.extension.jenkins.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.*
import org.springframework.stereotype.Component

@Component
class JenkinsNotificationChannel(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
    private val jenkinsClientFactory: JenkinsClientFactory,
) : AbstractNotificationChannel<JenkinsNotificationChannelConfig>(
    JenkinsNotificationChannelConfig::class
) {
    override fun publish(config: JenkinsNotificationChannelConfig, event: Event): NotificationResult {
        // Computing the expansion parameters
        val templateParameters = event.getTemplateParameters(caseVariants = true)
        // Gets the Jenkins configuration
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
            ?: return NotificationResult.invalidConfiguration("Jenkins configuration cannot be found: ${config.config}")
        // Gets the Jenkins client
        val jenkinsClient = jenkinsClientFactory.getClient(jenkinsConfig)
        // Running the job
        val job = SimpleExpand.expand(config.job, templateParameters)
        val parameters = config.parameters.map {
            it.name to SimpleExpand.expand(it.value, templateParameters)
        }.toMap()
        val error = when (config.callMode) {
            JenkinsNotificationChannelConfigCallMode.ASYNC -> launchAsync(jenkinsClient, job, parameters)
            JenkinsNotificationChannelConfigCallMode.SYNC -> launchSync(jenkinsClient, job, config.timeout, parameters)
        }
        // In case of error
        val jobUrl = jenkinsClient.getJob(job).url
        return if (error != null) {
            NotificationResult.error(error, id = jobUrl)
        }
        // OK
        else {
            NotificationResult.ok(id = jobUrl)
        }
    }

    private fun launchSync(
        jenkinsClient: JenkinsClient,
        job: String,
        timeout: Int,
        parameters: Map<String, String>
    ): String? {
        val interval = 30 // seconds
        val retries = timeout / interval
        val build = jenkinsClient.runJob(
            job = job,
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
        job: String,
        parameters: Map<String, String>
    ): String? {
        val queueURI = jenkinsClient.fireAndForgetJob(
            job = job,
            parameters = parameters,
        )
        return if (queueURI != null) {
            null // No error
        } else {
            "Could not find job at $job"
        }
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            JenkinsNotificationChannelConfig::config.name to text,
        ).asJson()

    override val type: String = "jenkins"
    override val enabled: Boolean = true

    override fun getForm(c: JenkinsNotificationChannelConfig?): Form = Form.create()
        // Selection of configuration
        .selectionOfString(
            property = JenkinsNotificationChannelConfig::config,
            items = jenkinsConfigurationService.configurations.map { it.name },
            value = c?.config
        )
        // Job
        .textField(JenkinsNotificationChannelConfig::job, c?.job)
        // Parameters
        .multiform(
            property = JenkinsNotificationChannelConfig::parameters,
            items = c?.parameters,
        ) {
            Form.create()
                .textField(JenkinsNotificationChannelConfigParam::name, null)
                .textField(JenkinsNotificationChannelConfigParam::value, null)
        }
        // Call mode
        .enumField(JenkinsNotificationChannelConfig::callMode, c?.callMode)
        // Timeout
        .intField(JenkinsNotificationChannelConfig::timeout, c?.timeout, min = 1)

    override fun toText(config: JenkinsNotificationChannelConfig): String {
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
        return if (jenkinsConfig != null) {
            "${jenkinsConfig.url}/${config.job}"
        } else {
            "n/a"
        }
    }

}