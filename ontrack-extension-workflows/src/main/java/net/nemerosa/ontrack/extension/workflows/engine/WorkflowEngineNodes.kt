package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.generateRandomString
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.execution.core.PauseWorkflowNodeExecutorData
import net.nemerosa.ontrack.json.asJson

fun pauseNode(pauseMs: Long) = WorkflowNode(
    id = "pause-${generateRandomString(8)}",
    description = "(generated) Pausing $pauseMs milliseconds",
    executorId = "pause",
    data = PauseWorkflowNodeExecutorData(pauseMs).asJson(),
)
