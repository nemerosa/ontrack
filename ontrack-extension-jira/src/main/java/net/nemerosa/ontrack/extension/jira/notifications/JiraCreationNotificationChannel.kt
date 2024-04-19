package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.transform
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.multiStrings
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.form.yesNoField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JiraCreationNotificationChannel(
    private val jiraConfigurationService: JIRAConfigurationService,
    private val jiraSessionFactory: JIRASessionFactory,
    private val eventTemplatingService: EventTemplatingService,
    private val jiraNotificationEventRenderer: JiraNotificationEventRenderer,
) : AbstractNotificationChannel<JiraCreationNotificationChannelConfig, JiraCreationNotificationChannelOutput>(
    JiraCreationNotificationChannelConfig::class
) {

    private val logger: Logger = LoggerFactory.getLogger(JiraCreationNotificationChannel::class.java)

    override fun publish(
        config: JiraCreationNotificationChannelConfig,
        event: Event,
        template: String?,
        outputProgressCallback: (current: JiraCreationNotificationChannelOutput) -> JiraCreationNotificationChannelOutput,
    ): NotificationResult<JiraCreationNotificationChannelOutput> {
        // Getting the Jira configuration
        val jiraConfig: JIRAConfiguration = jiraConfigurationService.getConfiguration(config.configName)

        // Output
        var output = outputProgressCallback(JiraCreationNotificationChannelOutput())

        // Expanded labels
        val expandedLabels = config.labels.map {
            eventTemplatingService.renderEvent(
                event = event,
                template = it,
                renderer = PlainEventRenderer.INSTANCE,
            )
        }

        output = outputProgressCallback(output.withLabels(expandedLabels))

        // Gets the Jira client
        val jiraClient = jiraSessionFactory.create(jiraConfig).client

        // Checking for the existence of the ticket
        if (config.useExisting) {
            var jql = """project = "${config.projectName}" AND issuetype = "${config.issueType}""""
            expandedLabels.forEach { label ->
                jql += """ AND labels = "$label""""
            }
            output = outputProgressCallback(output.withJql(jql))
            val existingStub = jiraClient.searchIssueStubs(jiraConfig, jql).firstOrNull()
            if (existingStub != null) {
                return NotificationResult.ok(output.withStub(existingStub).withExisting(true))
            } else {
                output = outputProgressCallback(output.withExisting(false))
            }
        }

        // Title
        val title = eventTemplatingService.renderEvent(
            event = event,
            template = config.titleTemplate,
            renderer = PlainEventRenderer.INSTANCE
        )
        output = outputProgressCallback(output.withTitle(title))

        // Fix version
        val fixVersion = config.fixVersion
            ?.takeIf { it.isNotBlank() }
            ?.let {
                eventTemplatingService.renderEvent(
                    event = event,
                    template = it,
                    renderer = PlainEventRenderer.INSTANCE
                )
            }
        output = outputProgressCallback(output.withFixVersion(fixVersion))

        // Custom fields
        val customFields = config.customFields.map { (name, value) ->
            JiraCustomField(name, value.transform { text ->
                eventTemplatingService.renderEvent(
                    event = event,
                    template = text,
                    renderer = PlainEventRenderer.INSTANCE
                )
            })
        }
        output = outputProgressCallback(output.withCustomFields(customFields))

        // Body
        val body = eventTemplatingService.renderEvent(
            event = event,
            template = template,
            renderer = jiraNotificationEventRenderer,
        )
        output = outputProgressCallback(output.withBody(body))

        // Creates the issue
        val jiraIssueStub = jiraClient.createIssue(
            configuration = jiraConfig,
            project = config.projectName,
            issueType = config.issueType,
            labels = expandedLabels,
            fixVersion = fixVersion,
            assignee = config.assignee,
            title = title,
            customFields = customFields,
            body = body,
        )

        // OK
        return NotificationResult.ok(output.withStub(jiraIssueStub))
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            JiraCreationNotificationChannelConfig::titleTemplate.name to text
        ).asJson()

    override val type: String = "jira-creation"

    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: JiraCreationNotificationChannelConfig?): Form =
        Form.create()
            .textField(JiraCreationNotificationChannelConfig::configName, c?.configName)
            .yesNoField(JiraCreationNotificationChannelConfig::useExisting, c?.useExisting)
            .textField(JiraCreationNotificationChannelConfig::projectName, c?.projectName)
            .textField(JiraCreationNotificationChannelConfig::issueType, c?.issueType)
            .multiStrings(JiraCreationNotificationChannelConfig::labels, c?.labels)
            .textField(JiraCreationNotificationChannelConfig::fixVersion, c?.fixVersion)
            .textField(JiraCreationNotificationChannelConfig::assignee, c?.assignee)
            .textField(JiraCreationNotificationChannelConfig::titleTemplate, c?.titleTemplate)

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: JiraCreationNotificationChannelConfig): String =
        config.titleTemplate

}