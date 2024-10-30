package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.model.exceptions.InputException

class SlotPipelineDataFieldMissingException(name: String) : InputException(
    """Missing field in input: $name"""
)