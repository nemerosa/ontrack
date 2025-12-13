package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.model.exceptions.InputException

class SCMEngineNotDetectedException : InputException("SCM engine could not be determined from the environment.")
