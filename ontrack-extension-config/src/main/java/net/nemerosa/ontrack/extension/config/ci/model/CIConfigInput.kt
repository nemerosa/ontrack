package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("CI configuration")
data class CIConfigInput(
    @APIDescription("Version of the configuration")
    val version: String,
    @APIDescription("Configuration")
    val configuration: CIConfigRoot = CIConfigRoot(),
)