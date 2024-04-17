package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode

data class JiraCustomField(
    val name: String,
    val value: JsonNode,
)
