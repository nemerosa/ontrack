package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JiraCreationNotificationChannelConfig(
    @APIDescription("Name of the Jira configuration to use for the connection")
    val configName: String,
    @APIDescription("If true, no ticket is created if it exists already")
    val useExisting: Boolean,
    @APIDescription("Key of the Jira project where to create the ticket")
    val projectName: String,
    @APIDescription("Name of the issue type to use for the ticket")
    val issueType: String,
    @APIDescription("List of labels for the ticket")
    val labels: List<String> = emptyList(),
    @APIDescription("Name of the fix version to assign to the ticket")
    val fixVersion: String? = null,
    @APIDescription("Username of the assignee")
    val assignee: String? = null,
    @APIDescription("(template) Summary of the ticket")
    val titleTemplate: String,
    @APIDescription("List of custom fields for the ticket")
    @DocumentationList
    val customFields: List<JiraCustomField> = emptyList(),
)
