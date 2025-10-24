package net.nemerosa.ontrack.extension.config.ci.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Configuration for a promotion")
data class CIPromotionConfig(
    @APIDescription("List of validation stamps to get for this promotion")
    val validations: List<String> = emptyList(),
    @APIDescription("List of promotion levels to get for this promotion")
    val promotions: List<String> = emptyList(),
)
