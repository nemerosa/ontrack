package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Custom configurations")
data class CICustom(
    @APIDescription("Custom configurations with conditions")
    val configs: List<CICustomConfig> = emptyList(),
)
