package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.PromotionLevel

class PreviousPromotionRequiredGlobalException(
        previousPromotion: PromotionLevel,
        promotion: PromotionLevel
) : InputException(
        """
           The "Previous Promotion Condition" settings prevent
           the ${promotion.name} to be granted because the ${previousPromotion.name} promotion
           has not been granted.
        """.trimIndent()
)
