package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription

data class JiraLinkNotificationChannelOutput(
    @APIDescription("Source ticket")
    val sourceTicket: String,
    @APIDescription("Target ticket")
    val targetTicket: String,
)
