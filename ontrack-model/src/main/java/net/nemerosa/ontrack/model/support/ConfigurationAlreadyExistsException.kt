package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.exceptions.InputException

class ConfigurationAlreadyExistsException(name: String) : InputException(
    "Configuration already exists with same name: $name"
)
