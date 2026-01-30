package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("Custom configuration with conditions")
data class CICustomConfig(
    @APIDescription("List of conditions")
    val conditions: List<CIConditionConfig> = emptyList(),
    @APIDescription("Project configuration")
    val project: CIProjectConfig = CIProjectConfig(),
    @APIDescription("Branch configuration")
    val branch: CIBranchConfig = CIBranchConfig(),
    @APIDescription("Build configuration")
    val build: CIBuildConfig = CIBuildConfig(),
)
