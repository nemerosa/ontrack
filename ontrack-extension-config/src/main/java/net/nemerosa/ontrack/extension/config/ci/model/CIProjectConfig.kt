package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.model.ProjectIssueServiceIdentifier
import net.nemerosa.ontrack.model.annotations.APIDescription

data class CIProjectConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
    val name: String? = null,
    @APIDescription("Name of the SCM configuration to use for this project")
    val scmConfig: String? = null,
    val issueServiceIdentifier: ProjectIssueServiceIdentifier? = null,
    val scmIndexationInterval: Int? = null,
) : CIPropertiesConfig
