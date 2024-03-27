package net.nemerosa.ontrack.extension.dm.data

import java.time.LocalDateTime

data class EndToEndPromotionFilter(
    val minDepth: Int = 1,
    val maxDepth: Int = 50,
    val afterTime: LocalDateTime? = null,
    val beforeTime: LocalDateTime? = null,
    val samePromotion: Boolean = true,
    val promotionId: Int? = null,
    val targetPromotionId: Int? = null,
    val refProject: String? = null,
    val targetProject: String? = null,
    val buildOrder: Boolean? = true,
)