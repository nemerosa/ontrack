package net.nemerosa.ontrack.extension.config.ci.conditions

import net.nemerosa.ontrack.model.exceptions.InputException

class ConditionConfigException(name: String) : InputException("Condition $name has an invalid configuration.")
