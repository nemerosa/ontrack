package net.nemerosa.ontrack.extension.workflows.execution

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowNodeExecutorResult(
    val type: WorkflowNodeExecutorResultType,
    val message: String?,
    val output: JsonNode?,
    val context: Map<String, JsonNode> = emptyMap(),
) {
    companion object {

        fun error(message: String, output: JsonNode?) = WorkflowNodeExecutorResult(
            type = WorkflowNodeExecutorResultType.ERROR,
            message = message,
            output = output,
        )

        fun success(output: JsonNode?, context: Map<String, JsonNode> = emptyMap()) = WorkflowNodeExecutorResult(
            type = WorkflowNodeExecutorResultType.SUCCESS,
            message = null,
            output = output,
            context = context,
        )

    }
}
