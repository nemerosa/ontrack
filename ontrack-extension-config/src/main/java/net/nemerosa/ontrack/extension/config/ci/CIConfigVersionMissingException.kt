package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigVersionMissingException: InputException("Configuration file is missing its version.")
