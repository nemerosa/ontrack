package net.nemerosa.ontrack.extension.dm.charts

/**
 * @param refPromotionId Promotion to start from
 * @param targetProject Project to go to
 */
data class E2EChartParameters(
    val refPromotionId: Int,
    val samePromotion: Boolean,
    val targetPromotionId: Int?,
    val targetProject: String,
)