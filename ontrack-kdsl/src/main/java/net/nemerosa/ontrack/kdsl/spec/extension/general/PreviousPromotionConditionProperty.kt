package net.nemerosa.ontrack.kdsl.spec.extension.general

import net.nemerosa.ontrack.kdsl.spec.PromotionLevel
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

/**
 * Sets the previous promotion condition on a promotion level: whether it can be granted before the
 * level immediately below it in the branch's order.
 *
 * The property is supported on the project and the branch too - the server resolves it through a
 * cascade - but only the promotion level is exposed here, because that is the one level at which
 * setting it is a statement about one promotion rather than about every ladder underneath.
 */
var PromotionLevel.previousPromotionCondition: Boolean?
    get() = getProperty(PREVIOUS_PROMOTION_CONDITION_PROPERTY)?.path("previousPromotionRequired")?.asBoolean()
    set(value) {
        if (value != null) {
            setProperty(PREVIOUS_PROMOTION_CONDITION_PROPERTY, mapOf("previousPromotionRequired" to value))
        } else {
            deleteProperty(PREVIOUS_PROMOTION_CONDITION_PROPERTY)
        }
    }

const val PREVIOUS_PROMOTION_CONDITION_PROPERTY =
    "net.nemerosa.ontrack.extension.general.PreviousPromotionConditionPropertyType"
