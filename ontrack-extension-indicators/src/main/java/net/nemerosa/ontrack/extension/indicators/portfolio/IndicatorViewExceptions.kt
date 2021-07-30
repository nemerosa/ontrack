package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException

class IndicatorViewIDNotFoundException(id: String) : NotFoundException(
    """Indicator view with ID $id cannot be found."""
)

class IndicatorViewNameAlreadyExistsException(name: String) : InputException(
    """Indicator view with name "$name" already exists."""
)

class IndicatorViewIDMismatchException(expectedId: String, actualId: String) : InputException(
    "Indicator view ID mismatch. Expected $expectedId, got $actualId"
)
