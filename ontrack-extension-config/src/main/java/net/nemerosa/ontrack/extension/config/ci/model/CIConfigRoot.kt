package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("CI configuration root")
data class CIConfigRoot(
    @APIDescription("Configuration to apply by default")
    val defaults: CIConfig = CIConfig(),
    @APIDescription("Configuration to apply with some conditions")
    val custom: CICustom = CICustom(),
)