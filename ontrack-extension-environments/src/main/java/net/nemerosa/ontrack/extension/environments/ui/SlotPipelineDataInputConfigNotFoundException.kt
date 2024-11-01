package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.model.exceptions.InputException

class SlotPipelineDataInputConfigNotFoundException(id: String): InputException(
    """Configured admission rule ID not found: $id"""
)
