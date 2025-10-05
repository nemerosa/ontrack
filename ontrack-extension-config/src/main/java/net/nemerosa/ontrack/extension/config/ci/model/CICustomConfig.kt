package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode

data class CICustomConfig(
    val conditions: Map<String, JsonNode> = emptyMap(),
    val project: CIProjectConfig = CIProjectConfig(),
    val branch: CIBranchConfig = CIBranchConfig(),
    val build: CIBuildConfig = CIBuildConfig(),
)
