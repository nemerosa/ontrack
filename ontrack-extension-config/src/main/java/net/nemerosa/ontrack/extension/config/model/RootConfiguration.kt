package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Root configuration, containing the default configurations and the customizations.")
data class RootConfiguration(
    @APIDescription("Default configuration to use, outside of any customization.")
    val defaults: Configuration = Configuration(),
    // TODO Custom configurations
)
