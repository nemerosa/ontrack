package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BuildConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
) : PropertiesConfiguration
