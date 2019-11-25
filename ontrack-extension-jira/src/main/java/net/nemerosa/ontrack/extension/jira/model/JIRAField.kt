package net.nemerosa.ontrack.extension.jira.model

import com.fasterxml.jackson.databind.JsonNode
import lombok.Data

class JIRAField(
        val id: String,
        val name: String,
        val value: JsonNode
)
