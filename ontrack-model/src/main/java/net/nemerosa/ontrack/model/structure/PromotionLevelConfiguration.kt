package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.api.APIDescription

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
) {
    fun merge(other: PromotionLevelConfiguration) = PromotionLevelConfiguration(
        name = name,
        description = other.description.takeIf { it.isNotBlank() } ?: description,
        validations = (validations + other.validations).distinct(),
        promotions = (promotions + other.promotions).distinct(),
    )
}