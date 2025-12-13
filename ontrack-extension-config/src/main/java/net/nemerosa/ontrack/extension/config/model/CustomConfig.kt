package net.nemerosa.ontrack.extension.config.model

data class CustomConfig(
    val conditions: List<ConditionConfig> = emptyList(),
    val project: ProjectConfiguration = ProjectConfiguration(),
    val branch: BranchConfiguration = BranchConfiguration(),
    val build: BuildConfiguration = BuildConfiguration(),
)
