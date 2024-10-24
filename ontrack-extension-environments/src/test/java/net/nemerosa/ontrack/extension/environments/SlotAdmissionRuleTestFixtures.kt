package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.rules.core.*
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

    fun testEnvironmentAdmissionRuleConfig(
        slot: Slot,
        previousSlot: Slot,
        qualifier: String? = null,
    ) = SlotAdmissionRuleConfig(
        slot = slot,
        name = "",
        description = null,
        ruleId = EnvironmentSlotAdmissionRule.ID,
        ruleConfig = EnvironmentSlotAdmissionRuleConfig(
            environmentName = previousSlot.environment.name,
            qualifier = qualifier ?: previousSlot.qualifier,
        ).asJson(),
    )
}