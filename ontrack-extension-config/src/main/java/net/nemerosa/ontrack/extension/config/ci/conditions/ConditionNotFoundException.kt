package net.nemerosa.ontrack.extension.config.ci.conditions

import net.nemerosa.ontrack.model.exceptions.InputException

class ConditionNotFoundException(name: String) : InputException("Condition with name $name cannot be found.")
