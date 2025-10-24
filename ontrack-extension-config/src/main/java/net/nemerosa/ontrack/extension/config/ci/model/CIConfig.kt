package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Configurations for the project, branch and build")
data class CIConfig(
    @APIDescription("Project configuration")
    val project: CIProjectConfig = CIProjectConfig(),
    @APIDescription("Branch configuration")
    val branch: CIBranchConfig = CIBranchConfig(),
    @APIDescription("Build configuration")
    val build: CIBuildConfig = CIBuildConfig(),
)