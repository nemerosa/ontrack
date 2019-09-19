package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * Check on a promotion run for it creation.
 */
interface PromotionRunCheckExtension : Extension {

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
