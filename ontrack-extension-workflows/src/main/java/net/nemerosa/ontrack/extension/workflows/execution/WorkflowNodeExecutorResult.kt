package net.nemerosa.ontrack.extension.workflows.execution

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowNodeExecutorResult(
    val type: WorkflowNodeExecutorResultType,
    val message: String?,
    val output: JsonNode?,
) {
    companion object {

        fun error(message: String, output: JsonNode?) = WorkflowNodeExecutorResult(
            type = WorkflowNodeExecutorResultType.ERROR,
            message = message,
            output = output,
        )

        fun success(output: JsonNode?) = WorkflowNodeExecutorResult(
            type = WorkflowNodeExecutorResultType.SUCCESS,
            message = null,
            output = output,
        )

    }
}
