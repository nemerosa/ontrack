package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription

data class JiraLinkNotificationChannelConfig(
    @APIDescription("Name of the Jira configuration to use for the connection")
    val configName: String,
    @APIDescription("JQuery to get the source ticket")
    val sourceQuery: String,
    @APIDescription("JQuery to get the target ticket")
    val targetQuery: String,
    @APIDescription("Name of the link")
    val linkName: String,
)
