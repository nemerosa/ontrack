package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.BaseException

class SlotPipelineIdNotFoundException(id: String) : BaseException(
    "Slot pipeline '$id' was not found."
)

