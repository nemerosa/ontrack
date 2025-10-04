package net.nemerosa.ontrack.extension.config.ci.engine

import net.nemerosa.ontrack.model.exceptions.InputException

class CIEngineNotFoundException(name: String) : InputException("CI engine with name $name cannot be found.")