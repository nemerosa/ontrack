package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BranchConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    val validations: List<ValidationStampConfiguration> = emptyList(),
) : PropertiesConfiguration
