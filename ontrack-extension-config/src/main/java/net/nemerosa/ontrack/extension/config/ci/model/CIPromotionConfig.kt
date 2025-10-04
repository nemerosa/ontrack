package net.nemerosa.ontrack.extension.config.ci.model

data class CIPromotionConfig(
    val validations: List<String> = emptyList(),
    val promotions: List<String> = emptyList(),
)
