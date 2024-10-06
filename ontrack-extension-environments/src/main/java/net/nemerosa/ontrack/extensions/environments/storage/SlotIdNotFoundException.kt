package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.common.BaseException

class SlotIdNotFoundException(id: String) : BaseException(
    "Slot ID '$id' not found"
)
