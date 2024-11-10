package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

const val BUILD_PROMOTION_INFO_PROMOTION_LEVEL = "promotionLevel"
const val BUILD_PROMOTION_INFO_PROMOTION_RUN = "promotionRun"

fun buildPromotionInfoItemForPromotionLevel(promotionLevel: PromotionLevel) = BuildPromotionInfoItem(
    type = BUILD_PROMOTION_INFO_PROMOTION_LEVEL,
    data = promotionLevel,
)

fun buildPromotionInfoItemForPromotionRun(promotionRun: PromotionRun) = BuildPromotionInfoItem(
    type = BUILD_PROMOTION_INFO_PROMOTION_RUN,
    data = promotionRun,
)
