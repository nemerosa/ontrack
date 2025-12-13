package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Custom configuration with conditions")
data class CICustomConfig(
    @APIDescription("List of conditions")
    val conditions: Map<String, JsonNode> = emptyMap(),
    @APIDescription("Project configuration")
    val project: CIProjectConfig = CIProjectConfig(),
    @APIDescription("Branch configuration")
    val branch: CIBranchConfig = CIBranchConfig(),
    @APIDescription("Build configuration")
    val build: CIBuildConfig = CIBuildConfig(),
)
