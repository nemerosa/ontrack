package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.common.BaseException

class SlotWorkflowIdNotFoundException(id: String) : BaseException(
    "Slot workflow '$id' was not found."
)

