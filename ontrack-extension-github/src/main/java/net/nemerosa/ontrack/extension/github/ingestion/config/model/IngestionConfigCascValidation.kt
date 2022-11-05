package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

data class IngestionConfigCascValidation(
    @APIDescription("Unique name for the validation stamp in the branch")
    val name: String,
    @APIDescription("Optional description")
    val description: String? = null,
    @APIDescription("Data type")
    val dataType: IngestionConfigCascValidationType? = null,
    @APIDescription("Reference to the image to set")
    val image: String? = null,
)