package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.BaseException

class SlotIdNotFoundException(id: String) : BaseException(
    "Slot ID '$id' not found"
)
