package net.nemerosa.ontrack.extension.environments.promotions

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Build

@APIDescription("Number of environments where a build is deployed")
data class EnvironmentBuildCount(
    @APIDescription("Associated build")
    val build: Build,
    @APIDescription("Number of environments where a build is deployed")
    val count: Int,
)
