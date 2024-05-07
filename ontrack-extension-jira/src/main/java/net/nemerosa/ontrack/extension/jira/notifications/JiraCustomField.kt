package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.SelfDocumented

@SelfDocumented
data class JiraCustomField(
    @APIDescription("Name of the field")
    val name: String,
    @APIDescription("Value for the field, as understood by the Jira API")
    val value: JsonNode,
)
