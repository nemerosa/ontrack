package net.nemerosa.ontrack.extension.config.ci.conditions

import org.springframework.stereotype.Component

@Component
class ConditionRegistry(
    conditions: List<Condition>,
) {

    private val index = conditions.associateBy { it.name }

    fun findCondition(name: String): Condition? = index[name]
    fun getCondition(name: String): Condition =
        findCondition(name) ?: throw ConditionNotFoundException(name)

}