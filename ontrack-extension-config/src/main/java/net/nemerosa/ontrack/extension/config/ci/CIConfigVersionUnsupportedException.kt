package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigVersionUnsupportedException(version: String) :
    InputException("Configuration file version is not supported: $version.")
