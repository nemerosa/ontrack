package net.nemerosa.ontrack.extension.environments.rules

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRule

interface SlotAdmissionRuleRegistry {

    val rules: List<SlotAdmissionRule<*, *>>

    fun getRule(ruleId: String): SlotAdmissionRule<*, *>

}