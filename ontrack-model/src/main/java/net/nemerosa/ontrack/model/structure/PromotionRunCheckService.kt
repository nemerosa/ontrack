package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.exceptions.InputException

/**
 * This service is called to check if a promotion run can actually be created or not.
 */
interface PromotionRunCheckService {

    /**
     * Checks if the given [promotionRun] can be created or not. Throws an [InputException] if the
     * promotion run cannot be created.
     */
    @Throws(InputException::class)
    fun checkPromotionRunCreation(promotionRun: PromotionRun)

}