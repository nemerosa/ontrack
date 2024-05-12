package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.BaseException
import java.time.Duration

class WorkflowExecutionTimeoutException(timeout: Duration) : BaseException(
    "Workflow node execution timed out after $timeout."
)