package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class ValidationStampDataConfigurationWrongFormatException(name: String) : InputException(
    """Validation stamp data configuration for $name is incorrect."""
)
