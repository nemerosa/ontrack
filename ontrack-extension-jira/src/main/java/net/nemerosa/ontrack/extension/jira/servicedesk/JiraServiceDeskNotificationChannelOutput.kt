package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import net.nemerosa.ontrack.model.annotations.API
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JiraServiceDeskNotificationChannelOutput(
    @APIDescription("ID of the Service Desk where the ticket has been created")
    val serviceDeskId: Int,
    @APIDescription("ID of the Request Type of the created ticket")
    val requestTypeId: Int,
    @APIDescription("True if the ticket was already existing")
    val existing: Boolean? = null,
    @APIDescription("List of actual fields which have been set")
    @DocumentationList
    val fields: List<JiraCustomField>? = null,
    @APIDescription("Key of the created ticket")
    val ticketKey: String? = null,
    @APIDescription("URL to the created ticket")
    val url: String? = null,
) {

    fun withFields(fields: List<JiraCustomField>) = JiraServiceDeskNotificationChannelOutput(
        serviceDeskId, requestTypeId, existing, fields, ticketKey, url
    )

    fun withExisting(existing: Boolean) = JiraServiceDeskNotificationChannelOutput(
        serviceDeskId, requestTypeId, existing, fields, ticketKey, url
    )

    fun withStub(stub: JIRAIssueStub) = JiraServiceDeskNotificationChannelOutput(
        serviceDeskId, requestTypeId, existing, fields, stub.key, stub.url
    )

}
