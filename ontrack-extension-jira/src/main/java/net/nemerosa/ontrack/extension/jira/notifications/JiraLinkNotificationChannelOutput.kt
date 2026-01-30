package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.common.api.APIDescription

data class JiraLinkNotificationChannelOutput(
    @APIDescription("Source ticket")
    val sourceTicket: String,
    @APIDescription("Target ticket")
    val targetTicket: String,
)
