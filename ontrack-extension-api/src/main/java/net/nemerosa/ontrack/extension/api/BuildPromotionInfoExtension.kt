package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import kotlin.reflect.KClass

/**
 * Extension to get information about a build promotions.
 */
interface BuildPromotionInfoExtension : Extension {

    val types: Collection<KClass<*>>

    fun buildPromotionInfoItemsWithNoPromotion(build: Build): List<BuildPromotionInfoItem<*>> = emptyList()

    fun buildPromotionInfoItemsAfterPromotion(
        build: Build,
        promotionLevel: PromotionLevel
    ): List<BuildPromotionInfoItem<*>> =
        emptyList()

    fun buildPromotionInfoItemsBeforePromotion(
        build: Build,
        promotionLevel: PromotionLevel
    ): List<BuildPromotionInfoItem<*>> =
        emptyList()

}