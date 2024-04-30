package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField

data class JiraServiceDeskNotificationChannelConfig(
    val configName: String,
    val useExisting: Boolean,
    val serviceDeskId: Int,
    val requestTypeId: Int,
    val fields: List<JiraCustomField>,
    val searchTerm: String?,
)
