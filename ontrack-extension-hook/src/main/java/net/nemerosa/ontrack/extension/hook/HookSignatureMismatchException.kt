package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.exceptions.InputException

class HookSignatureMismatchException : InputException(
    """Hook signature does not match."""
)