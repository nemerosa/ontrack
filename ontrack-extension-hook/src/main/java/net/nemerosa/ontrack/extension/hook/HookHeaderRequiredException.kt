package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.exceptions.InputException

class HookHeaderRequiredException(name: String) : InputException(
    """Request header `$name` is required."""
)
