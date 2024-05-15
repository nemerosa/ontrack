package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JiraServiceDeskNotificationChannelConfig(
    @APIDescription("Name of the Jira configuration to use for the connection")
    val configName: String,
    @APIDescription("If true, no ticket is created if it exists already")
    val useExisting: Boolean,
    @APIDescription("If looking for existing tickets, which type of requests to look for (ALL by default)")
    val requestStatus: JiraServiceDeskRequestStatus? = null,
    @APIDescription("ID of the Service Desk where to create the ticket")
    val serviceDeskId: Int,
    @APIDescription("ID of the Request Type of the ticket to create")
    val requestTypeId: Int,
    @APIDescription("List of fields to set into the service desk ticket")
    @DocumentationList
    val fields: List<JiraCustomField>,
    @APIDescription("Search token to use to identify any existing ticket. This is a template.")
    val searchTerm: String?,
)
