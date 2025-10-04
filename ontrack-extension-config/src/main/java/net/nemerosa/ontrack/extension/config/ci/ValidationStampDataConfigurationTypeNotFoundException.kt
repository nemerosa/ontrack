package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class ValidationStampDataConfigurationTypeNotFoundException(type: String) : InputException(
    """Validation stamp data configuration type not found: $type."""
)
