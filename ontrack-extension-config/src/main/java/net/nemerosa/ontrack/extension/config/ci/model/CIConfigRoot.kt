package net.nemerosa.ontrack.extension.config.ci.model

data class CIConfigRoot(
    val defaults: CIConfig = CIConfig(),
    val custom: CICustom = CICustom(),
)