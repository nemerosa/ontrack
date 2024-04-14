package net.nemerosa.ontrack.extension.jenkins.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.*

abstract class AbstractJenkinsNotificationChannel(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
    private val eventTemplatingService: EventTemplatingService,
) : AbstractNotificationChannel<JenkinsNotificationChannelConfig, JenkinsNotificationChannelOutput>(
    JenkinsNotificationChannelConfig::class
) {
    override fun publish(
        config: JenkinsNotificationChannelConfig,
        event: Event,
        template: String?
    ): NotificationResult<JenkinsNotificationChannelOutput> {
        // Gets the Jenkins configuration
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
            ?: return NotificationResult.invalidConfiguration("Jenkins configuration cannot be found: ${config.config}")
        // Gets the Jenkins client
        val jenkinsClient = createJenkinsClient(jenkinsConfig)
        // Running the job
        val job = eventTemplatingService.render(
            template = config.job,
            event = event,
            renderer = PlainEventRenderer.INSTANCE,
        )
        val parameters = config.parameters.associate {
            it.name to eventTemplatingService.render(
                template = it.value,
                event = event,
                renderer = PlainEventRenderer.INSTANCE
            )
        }
        val error = when (config.callMode) {
            JenkinsNotificationChannelConfigCallMode.ASYNC -> launchAsync(jenkinsClient, job, parameters)
            JenkinsNotificationChannelConfigCallMode.SYNC -> launchSync(jenkinsClient, job, config.timeout, parameters)
        }
        // Output
        val jobUrl = jenkinsClient.getJob(job).url
        val output = JenkinsNotificationChannelOutput(
            jobUrl = jobUrl,
            parameters = parameters.map { (name, value) -> JenkinsNotificationChannelConfigParam(name, value) }
        )
        // In case of error
        return if (error != null) {
            NotificationResult.error(
                message = error,
                output = output,
            )
        }
        // OK
        else {
            NotificationResult.ok(output = output)
        }
    }

    protected abstract fun createJenkinsClient(config: JenkinsConfiguration): JenkinsClient

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

    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
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
        .enumField(
            JenkinsNotificationChannelConfig::callMode,
            c?.callMode ?: JenkinsNotificationChannelConfigCallMode.ASYNC
        )
        // Timeout
        .intField(
            JenkinsNotificationChannelConfig::timeout,
            c?.timeout ?: JenkinsNotificationChannelConfig.DEFAULT_TIMEOUT,
            min = 1,
        )

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: JenkinsNotificationChannelConfig): String {
        val jenkinsConfig = jenkinsConfigurationService.findConfiguration(config.config)
        return if (jenkinsConfig != null) {
            "${jenkinsConfig.url}/${config.job}"
        } else {
            "n/a"
        }
    }

}