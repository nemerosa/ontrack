package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Validation stamp configuration")
data class ValidationStampConfiguration(
    @APIDescription("Name of the validation stamp")
    val name: String,
    @APIDescription("Description of the validation stamp")
    val description: String = "",
    @APIDescription("Data configuration")
    val validationStampDataConfiguration: ValidationStampDataConfiguration? = null,
)
