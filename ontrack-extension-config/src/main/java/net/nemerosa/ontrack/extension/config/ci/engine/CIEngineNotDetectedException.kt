package net.nemerosa.ontrack.extension.config.ci.engine

import net.nemerosa.ontrack.model.exceptions.InputException

class CIEngineNotDetectedException : InputException("CI engine could not be determined from the environment.")
