package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

data class IngestionConfigCascPromotion(
    @APIDescription("Unique name for the promotion in the branch")
    val name: String,
    @APIDescription("Optional description")
    val description: String? = null,
    @APIDescription("List of validations triggering this promotion. Important: these names are the names of the validations after step name resolution.")
    val validations: List<String> = emptyList(),
    @APIDescription("List of promotions triggering this promotion")
    val promotions: List<String> = emptyList(),
    @APIDescription("Regular expression to include validation stamps by name")
    val include: String? = null,
    @APIDescription("Regular expression to exclude validation stamps by name")
    val exclude: String? = null,
    @APIDescription("Reference to the image to set")
    val image: String? = null,
)