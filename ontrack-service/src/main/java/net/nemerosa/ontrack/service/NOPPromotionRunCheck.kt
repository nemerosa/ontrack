package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.PromotionRunCheck
import org.springframework.stereotype.Component

/**
 * Does not check anything ; just a placeholder.
 */
@Component
class NOPPromotionRunCheck : PromotionRunCheck {

    /**
     * Evaluated last.
     */
    override val order: Int = Int.MAX_VALUE

    override fun checkPromotionRunCreation(promotionRun: PromotionRun) {}
}
