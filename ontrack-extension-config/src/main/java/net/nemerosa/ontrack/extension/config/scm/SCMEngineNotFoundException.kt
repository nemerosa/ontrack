package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.model.exceptions.InputException

class SCMEngineNotFoundException(name: String) : InputException("SCM engine with name $name cannot be found.")