package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.exceptions.InputException

class IndicatorViewIDNotFoundException(id: String) : InputException(
    """Indicator view with ID $id cannot be found."""
)

class IndicatorViewNameAlreadyExistsException(name: String) : InputException(
    """Indicator view with name "$name" already exists."""
)
