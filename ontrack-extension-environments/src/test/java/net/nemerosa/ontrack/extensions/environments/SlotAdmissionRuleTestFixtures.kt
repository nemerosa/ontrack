package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.rules.core.PromotionSlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.rules.core.PromotionSlotAdmissionRuleConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid

object SlotAdmissionRuleTestFixtures {
    fun testPromotionAdmissionRuleConfig(
        slot: Slot,
        name: String = uid("rule-"),
        promotion: String = "GOLD",
    ) = SlotAdmissionRuleConfig(
        slot = slot,
        name = name,
        description = null,
        ruleId = PromotionSlotAdmissionRule.ID,
        ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = promotion).asJson(),
    )
}