package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.exceptions.InputException

class RunPayloadMissingFieldException(
    field: String,
) : InputException(
    """The payload field is expected to have a value: $field"""
)