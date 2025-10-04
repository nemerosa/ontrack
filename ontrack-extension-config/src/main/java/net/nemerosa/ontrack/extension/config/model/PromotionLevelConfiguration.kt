package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Promotion level configuration")
data class PromotionLevelConfiguration(
    @APIDescription("Name of the promotion level")
    val name: String,
    @APIDescription("Description of the promotion level")
    val description: String = "",
    @APIDescription("List of validations")
    val validations: List<String> = emptyList(),
    @APIDescription("List of promotions")
    val promotions: List<String> = emptyList(),
)
