package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.reducedStackTrace
import java.time.LocalDateTime

/**
 * Execution information about a node
 *
 * @property id ID of the node
 */
data class WorkflowInstanceNode(
    val id: String,
    val status: WorkflowInstanceNodeStatus,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val output: JsonNode?,
    val error: String?,
) {

    fun start(time: LocalDateTime = Time.now) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.STARTED,
        startTime = time,
        endTime = endTime,
        output = output,
        error = error,
    )

    fun success(output: JsonNode, time: LocalDateTime = Time.now) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.SUCCESS,
        startTime = startTime,
        endTime = time,
        output = output,
        error = null,
    )

    fun error(throwable: Throwable, time: LocalDateTime = Time.now) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.ERROR,
        startTime = startTime,
        endTime = time,
        output = null,
        error = reducedStackTrace(throwable),
    )
}
