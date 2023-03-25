package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.model.exceptions.InputException

class TFCHookPayloadParsingException: InputException(
    """Cannot parse the hook payload."""
)