package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.model.exceptions.InputException

class SlotIdAlreadyExistsException(id: String) : InputException(
    "Slot ID '$id' already exists."
)
