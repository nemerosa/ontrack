package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor

class WorkflowNodeExecutorNotEnabledException(executor: WorkflowNodeExecutor) : BaseException(
    """Executor is not enabled: ${executor.displayName} [${executor.id}]"""
)