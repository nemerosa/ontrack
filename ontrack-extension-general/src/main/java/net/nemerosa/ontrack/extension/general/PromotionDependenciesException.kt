package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.PromotionRun

class PromotionDependenciesException(
        promotionRun: PromotionRun,
        dependencies: List<String>,
        dependency: String
) : InputException(
        """
           The "Promotion Dependencies Condition" setup in ${promotionRun.promotionLevel.name} prevents
           the ${promotionRun.build.entityDisplayName} to be promoted because it requires the following promotions
           to be all granted (${dependencies.joinToString(",")}) and "$dependency" was not granted.
        """.trimIndent()
)
