package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("Root configuration, containing the default configurations and the customizations.")
data class RootConfiguration(
    @APIDescription("Default configuration to use, outside of any customization.")
    val defaults: Configuration = Configuration(),
    @APIDescription("Customization")
    val custom: Custom = Custom()
)
