package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class ConfigurableIndicatorTypeIdNotFoundException(id: String): NotFoundException(
    """Configurable indicator with id = $id cannot be found."""
)