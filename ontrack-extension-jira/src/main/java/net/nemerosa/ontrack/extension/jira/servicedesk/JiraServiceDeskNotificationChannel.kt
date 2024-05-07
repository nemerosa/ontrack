package net.nemerosa.ontrack.extension.jira.servicedesk

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import net.nemerosa.ontrack.extension.jira.notifications.JiraNotificationEventRenderer
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.transform
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.intField
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.form.yesNoField
import org.springframework.stereotype.Component

@Component
@APIDescription("This channel is used to create a Jira Service Desk ticket.")
@Documentation(JiraServiceDeskNotificationChannelConfig::class)
@Documentation(JiraServiceDeskNotificationChannelOutput::class, section = "output")
class JiraServiceDeskNotificationChannel(
    private val jiraConfigurationService: JIRAConfigurationService,
    private val jiraSessionFactory: JIRASessionFactory,
    private val eventTemplatingService: EventTemplatingService,
    private val jiraNotificationEventRenderer: JiraNotificationEventRenderer,
) : AbstractNotificationChannel<JiraServiceDeskNotificationChannelConfig, JiraServiceDeskNotificationChannelOutput>(
    JiraServiceDeskNotificationChannelConfig::class
) {

    override val type: String = "jira-service-desk"

    override val displayName: String = "Jira Service Desk"

    override val enabled: Boolean = true

    override fun publish(
        config: JiraServiceDeskNotificationChannelConfig,
        event: Event,
        template: String?,
        outputProgressCallback: (current: JiraServiceDeskNotificationChannelOutput) -> JiraServiceDeskNotificationChannelOutput
    ): NotificationResult<JiraServiceDeskNotificationChannelOutput> {

        // Getting the Jira configuration
        val jiraConfig: JIRAConfiguration = jiraConfigurationService.getConfiguration(config.configName)

        // Initial output
        var output = outputProgressCallback(
            JiraServiceDeskNotificationChannelOutput(
                serviceDeskId = config.serviceDeskId,
                requestTypeId = config.requestTypeId,
            )
        )

        // Gets the Jira client
        val jiraClient = jiraSessionFactory.create(jiraConfig).client
        val serviceDesk = jiraClient.serviceDesk

        // Checking for the existing of the ticket
        if (config.useExisting && !config.searchTerm.isNullOrBlank()) {
            // Rendering the search term
            val searchTerm = eventTemplatingService.renderEvent(
                event = event,
                template = config.searchTerm,
                renderer = PlainEventRenderer.INSTANCE,
            )
            // Query
            val existingStubs = serviceDesk.searchRequest(
                serviceDeskId = config.serviceDeskId,
                requestTypeId = config.requestTypeId,
                searchTerm = searchTerm
            )
            // If more than 1 result, we fail - too ambiguous to carry on
            if (existingStubs.size > 1) {
                error("More than one issue reported for search term: ${config.searchTerm}")
            }
            // If only 1 result, we return it
            else if (existingStubs.size == 1) {
                val existingStub = existingStubs.first()
                return NotificationResult.ok(output.withStub(existingStub).withExisting(true))
            }
            // If not result, we'll create the new request
            else {
                output = outputProgressCallback(output.withExisting(false))
            }
        }

        // Fields
        val fields = config.fields.map { (name, value) ->
            val renderer = if (name == "description") {
                jiraNotificationEventRenderer
            } else {
                PlainEventRenderer.INSTANCE
            }
            JiraCustomField(name, value.transform { text ->
                eventTemplatingService.renderEvent(
                    event = event,
                    template = text,
                    renderer = renderer,
                )
            })
        }
        output = outputProgressCallback(output.withFields(fields))

        // Creating the service desk request
        val stub = serviceDesk.createRequest(
            serviceDeskId = config.serviceDeskId,
            requestTypeId = config.requestTypeId,
            fields = fields,
        )

        // OK
        return NotificationResult.ok(
            output.withStub(stub)
        )
    }

    override fun toSearchCriteria(text: String): JsonNode = NullNode.instance

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: JiraServiceDeskNotificationChannelConfig?): Form = Form.create()
        .textField(JiraServiceDeskNotificationChannelConfig::configName, c?.configName)
        .yesNoField(JiraServiceDeskNotificationChannelConfig::useExisting, c?.useExisting)
        .intField(JiraServiceDeskNotificationChannelConfig::serviceDeskId, c?.serviceDeskId)
        .intField(JiraServiceDeskNotificationChannelConfig::requestTypeId, c?.requestTypeId)

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: JiraServiceDeskNotificationChannelConfig): String = ""
}