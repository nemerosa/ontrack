package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Previous Promotion Conditions")
data class PreviousPromotionConditionSettings(
        @APIDescription("Makes a promotion conditional based on the fact that a previous promotion has been granted.")
        val previousPromotionRequired: Boolean
)
