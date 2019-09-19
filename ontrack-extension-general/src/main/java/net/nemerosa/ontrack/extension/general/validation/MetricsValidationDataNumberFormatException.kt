package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.exceptions.InputException

class MetricsValidationDataNumberFormatException(
        name: String,
        value: String,
        ex: NumberFormatException
) : InputException(
        """Wrong format for the value of "$name": $value (${ex.message})"""
)