package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTracking
import net.nemerosa.ontrack.model.structure.PromotionRun

interface AutoVersioningDispatcher {

    fun dispatch(
        promotionRun: PromotionRun,
        tracking: AutoVersioningTracking,
    )

}