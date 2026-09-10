package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * Resolves the previous-promotion condition of a promotion level.
 *
 * The condition is configured by [PreviousPromotionConditionPropertyType] on the promotion level, its
 * branch or its project, and falls back to the global [PreviousPromotionConditionSettings]. The
 * cascade is *first found wins*, which means a `false` low down genuinely overrides a `true` higher
 * up rather than merely failing to add one: there is a real per-promotion-level answer, it just
 * usually comes from far away.
 *
 * It exists as a service because two things now ask the question - the promotion check which enforces
 * it, and the delivery map which draws it - and a map drawing a different subset from what the check
 * enforces would be worse than a map drawing nothing.
 */
interface PreviousPromotionConditionService {

    /**
     * Does reaching [promotionLevel] require its immediate predecessor in the branch's promotion
     * level order, and who says so?
     *
     * The question is about the *configuration*, not about a build: it is the same answer whether or
     * not any particular build already carries the predecessor promotion.
     */
    fun resolvePreviousPromotionCondition(promotionLevel: PromotionLevel): PreviousPromotionConditionResolution

}
