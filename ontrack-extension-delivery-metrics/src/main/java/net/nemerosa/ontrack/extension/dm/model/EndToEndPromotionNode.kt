package net.nemerosa.ontrack.extension.dm.model

import java.time.LocalDateTime

data class EndToEndPromotionNode(
    val project: String,
    val branch: String,
    val build: String,
    val buildCreation: LocalDateTime,
    val promotion: String?,
    val promotionCreation: LocalDateTime?,
)