package net.nemerosa.ontrack.extensions.environments.rules

import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule

interface SlotAdmissionRuleRegistry {

    val rules: List<SlotAdmissionRule<*, *>>

    fun getRule(ruleId: String): SlotAdmissionRule<*, *>

}