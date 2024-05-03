package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.BaseException

class WorkflowContextKeyNotFoundException(key: String) : BaseException(
    "Workflow context key not found: $key"
)
