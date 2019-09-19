package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PromotionLevel

class PreviousPromotionRequiredException(
        previousPromotion: PromotionLevel,
        promotion: PromotionLevel,
        entity: ProjectEntity
) : InputException(
        """
           The "Previous Promotion Condition" setup in ${entity.entityDisplayName} prevents
           the ${promotion.name} to be granted because the ${previousPromotion.name} promotion
           has not been granted.
        """.trimIndent()
)
