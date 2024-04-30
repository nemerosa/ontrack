package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField

data class JiraServiceDeskNotificationChannelOutput(
    val serviceDeskId: Int,
    val requestTypeId: Int,
    val existing: Boolean? = null,
    val fields: List<JiraCustomField>? = null,
    val ticketKey: String? = null,
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
