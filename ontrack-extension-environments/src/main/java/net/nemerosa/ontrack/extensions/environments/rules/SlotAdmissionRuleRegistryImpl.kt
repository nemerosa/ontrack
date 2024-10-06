package net.nemerosa.ontrack.extensions.environments.rules

import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
import org.springframework.stereotype.Component

@Component
class SlotAdmissionRuleRegistryImpl(
    rules: List<SlotAdmissionRule<*>>,
) : SlotAdmissionRuleRegistry {

    private val index = rules.associateBy { it.id }

    override fun getRule(ruleId: String): SlotAdmissionRule<*> =
        index[ruleId] ?: throw SlotAdmissionRuleIdNotFoundException(ruleId)
}