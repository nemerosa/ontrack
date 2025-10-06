package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.model.ProjectIssueServiceIdentifier

data class CIProjectConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
    val issueServiceIdentifier: ProjectIssueServiceIdentifier? = null,
) : CIPropertiesConfig
