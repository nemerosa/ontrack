package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.InputException

/**
 * Check on a promotion run for it creation.
 */
interface PromotionRunCheck {

    /**
     * Checks if the given [promotionRun] can be created or not. Throws an [InputException] if the
     * promotion run cannot be created.
     */
    @Throws(InputException::class)
    fun checkPromotionRunCreation(promotionRun: PromotionRun)

    /**
     * Order to the check. The lowest number is checked first.
     */
    val order: Int

}
