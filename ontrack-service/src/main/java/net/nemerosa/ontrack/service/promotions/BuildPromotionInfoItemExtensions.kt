package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

fun buildPromotionInfoItemForPromotionLevel(promotionLevel: PromotionLevel) = BuildPromotionInfoItem(
    promotionLevel = promotionLevel,
    data = promotionLevel,
)

fun buildPromotionInfoItemForPromotionRun(promotionRun: PromotionRun) = BuildPromotionInfoItem(
    promotionLevel = promotionRun.promotionLevel,
    data = promotionRun,
)
