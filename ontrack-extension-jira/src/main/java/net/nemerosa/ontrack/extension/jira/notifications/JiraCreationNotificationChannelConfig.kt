package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode

data class JiraCreationNotificationChannelConfig(
    val configName: String,
    val projectName: String,
    val issueType: String,
    val labels: List<String> = emptyList(),
    val fixVersion: String? = null,
    val assignee: String? = null,
    val titleTemplate: String,
    val customFields: Map<String, JsonNode> = emptyMap(),
)
