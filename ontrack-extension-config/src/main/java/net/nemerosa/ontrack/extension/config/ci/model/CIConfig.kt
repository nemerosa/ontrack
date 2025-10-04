package net.nemerosa.ontrack.extension.config.ci.model

data class CIConfig(
    val project: CIProjectConfig = CIProjectConfig(),
    val branch: CIBranchConfig = CIBranchConfig(),
    val build: CIBuildConfig = CIBuildConfig(),
)