package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.common.BaseException

class SlotWorkflowInstanceIdNotFoundException(id: String) : BaseException(
    "Slot workflow instance with id '$id' was not found."
)
