package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.textField
import org.springframework.stereotype.Component

@Component
@APIDescription("Linking two Jira tickets together")
@Documentation(JiraLinkNotificationChannelConfig::class)
@Documentation(JiraLinkNotificationChannelOutput::class, section = "output")
class JiraLinkNotificationChannel(
    private val jiraConfigurationService: JIRAConfigurationService,
    private val jiraSessionFactory: JIRASessionFactory,
    private val eventTemplatingService: EventTemplatingService,
) : AbstractNotificationChannel<JiraLinkNotificationChannelConfig, JiraLinkNotificationChannelOutput>(
    JiraLinkNotificationChannelConfig::class
) {

    override fun publish(
        recordId: String,
        config: JiraLinkNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?,
        outputProgressCallback: (current: JiraLinkNotificationChannelOutput) -> JiraLinkNotificationChannelOutput
    ): NotificationResult<JiraLinkNotificationChannelOutput> {

        // Getting the Jira configuration
        val jiraConfig: JIRAConfiguration = jiraConfigurationService.getConfiguration(config.configName)

        // Gets the Jira client
        val jiraClient = jiraSessionFactory.create(jiraConfig).client

        // Expansion of the queries
        val sourceQuery = eventTemplatingService.renderEvent(
            event = event,
            context = context,
            template = config.sourceQuery,
            renderer = PlainEventRenderer.INSTANCE,
        )
        val targetQuery = eventTemplatingService.renderEvent(
            event = event,
            context = context,
            template = config.targetQuery,
            renderer = PlainEventRenderer.INSTANCE,
        )

        // Looks for the source ticket
        val sourceTickets = jiraClient.searchIssueStubs(
            jiraConfiguration = jiraConfig,
            jql = sourceQuery,
        ).map { it.key }
        val sourceTicket = if (sourceTickets.isEmpty()) {
            return NotificationResult.error(
                message = "No ticket found for $sourceQuery"
            )
        } else if (sourceTickets.size > 1) {
            return NotificationResult.error(
                message = "Too many tickets found for $sourceQuery: $sourceTickets"
            )
        } else {
            sourceTickets.first()
        }

        // Looks for the target ticket
        val targetTickets = jiraClient.searchIssueStubs(
            jiraConfiguration = jiraConfig,
            jql = targetQuery,
        ).map { it.key }
        val targetTicket = if (targetTickets.isEmpty()) {
            return NotificationResult.error(
                message = "No ticket found for $targetQuery"
            )
        } else if (targetTickets.size > 1) {
            return NotificationResult.error(
                message = "Too many tickets found for $targetQuery: $targetTickets"
            )
        } else {
            targetTickets.first()
        }

        // Creating the link
        jiraClient.createLink(jiraConfig, sourceTicket, targetTicket, config.linkName)

        // OK
        return NotificationResult.ok(
            JiraLinkNotificationChannelOutput(
                sourceTicket = sourceTicket,
                targetTicket = targetTicket,
            )
        )
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(
            JiraLinkNotificationChannelConfig::linkName to text
        ).asJson()

    override val type: String = "jira-link"
    override val displayName: String = "Jira link creation"
    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: JiraLinkNotificationChannelConfig?): Form =
        Form.create()
            .textField(JiraLinkNotificationChannelConfig::sourceQuery, c?.sourceQuery)
            .textField(JiraLinkNotificationChannelConfig::targetQuery, c?.targetQuery)
            .textField(JiraLinkNotificationChannelConfig::linkName, c?.linkName)

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: JiraLinkNotificationChannelConfig): String = config.linkName
}