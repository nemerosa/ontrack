package net.nemerosa.ontrack.extension.dm.data

import java.time.LocalDateTime

data class EndToEndPromotionFilter(
    val minDepth: Int = 1,
    val maxDepth: Int = 50,
    val afterTime: LocalDateTime? = null,
    val samePromotion: Boolean = true,
    val refProject: String? = null,
    val targetProject: String? = null,
    val buildOrder: Boolean? = true,
)