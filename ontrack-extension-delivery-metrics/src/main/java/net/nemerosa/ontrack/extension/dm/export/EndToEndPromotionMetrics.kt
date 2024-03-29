package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord

object EndToEndPromotionMetrics {

    const val PROMOTION_LEAD_TIME = "ontrack_dm_promotion_lead_time"
    const val PROMOTION_SUCCESS_RATE = "ontrack_dm_promotion_success_rate"
    const val PROMOTION_TTR = "ontrack_dm_promotion_ttr"

    fun endToEndMetricTags(
        record: EndToEndPromotionRecord,
        promotion: String,
    ) = mapOf(
        "targetProject" to record.target.project,
        "sourceProject" to record.ref.project,
        "targetBranch" to record.target.branch,
        "sourceBranch" to record.ref.branch,
        "promotion" to promotion
    )

}