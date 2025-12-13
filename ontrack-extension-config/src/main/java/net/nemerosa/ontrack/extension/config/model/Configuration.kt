package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Representation of a configuration, used for defaults or subject to some condition.")
data class Configuration(
    @APIDescription("Configuration of the project")
    val project: ProjectConfiguration = ProjectConfiguration(),
    @APIDescription("Configuration of the branch")
    val branch: BranchConfiguration = BranchConfiguration(),
    @APIDescription("Configuration of the build")
    val build: BuildConfiguration = BuildConfiguration(),
)
