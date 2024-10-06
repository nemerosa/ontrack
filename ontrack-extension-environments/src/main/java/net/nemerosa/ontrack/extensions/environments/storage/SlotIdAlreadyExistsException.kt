package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.model.exceptions.InputException

class SlotIdAlreadyExistsException(id: String) : InputException(
    "Slot ID '$id' already exists."
)
