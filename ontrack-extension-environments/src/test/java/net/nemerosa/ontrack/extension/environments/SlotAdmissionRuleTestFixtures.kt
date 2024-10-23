package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.rules.core.BranchPatternSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.BranchPatternSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRuleConfig
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

    fun testBranchPatternAdmissionRuleConfig(
        slot: Slot,
        name: String = "Release branches only",
        includes: List<String> = listOf("release-.*"),
    ) = SlotAdmissionRuleConfig(
        slot = slot,
        name = name,
        description = null,
        ruleId = BranchPatternSlotAdmissionRule.ID,
        ruleConfig = BranchPatternSlotAdmissionRuleConfig(
            includes = includes,
        ).asJson(),
    )
}