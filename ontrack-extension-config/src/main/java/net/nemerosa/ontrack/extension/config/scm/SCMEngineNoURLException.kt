package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.model.exceptions.InputException

class SCMEngineNoURLException() : InputException("No SCM URL could be found in the environment.")
