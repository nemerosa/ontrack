package net.nemerosa.ontrack.extension.config.extensions

import net.nemerosa.ontrack.model.exceptions.InputException

class CIConfigExtensionNotFoundException(id: String) : InputException(
    """CI config extension not found: $id."""
)
