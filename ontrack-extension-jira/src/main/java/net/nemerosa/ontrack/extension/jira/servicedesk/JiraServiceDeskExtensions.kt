package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.client.JIRAClient

val JIRAClient.serviceDesk: JiraServiceDesk get() = JiraServiceDeskImpl(
    restTemplate
)