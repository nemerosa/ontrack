package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Input for a configuration")
data class ConfigurationInput(
    @APIDescription("Configuration to use")
    val configuration: RootConfiguration = RootConfiguration(),
)
