package net.nemerosa.ontrack.extensions.environments.rules

import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule

interface SlotAdmissionRuleRegistry {

    fun getRule(ruleId: String): SlotAdmissionRule<*>

}