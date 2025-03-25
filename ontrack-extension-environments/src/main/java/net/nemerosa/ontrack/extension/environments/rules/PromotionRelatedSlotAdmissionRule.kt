package net.nemerosa.ontrack.extension.environments.rules

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRule
import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * Slot admission rule linked to a promotion
 */
interface PromotionRelatedSlotAdmissionRule<C: Any, D> : SlotAdmissionRule<C, D> {

    /**
     * Is this rule related to the given promotion level?
     */
    fun isForPromotionLevel(ruleConfig: C, promotionLevel: PromotionLevel): Boolean
}