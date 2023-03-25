package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.model.exceptions.InputException

class TFCHookSignatureMismatchException : InputException(
    """Hook payload signature does not match."""
)
