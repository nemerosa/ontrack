package net.nemerosa.ontrack.extension.workflows.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class WorkflowInstanceRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {

    fun createInstance(instance: WorkflowInstance) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO WKF_INSTANCES(ID, TIMESTAMP, WORKFLOW, EVENT)
                VALUES (:id, :timestamp, CAST(:workflow AS JSONB), CAST(:event as JSONB))
            """.trimIndent(),
            mapOf(
                "id" to instance.id,
                "timestamp" to dateTimeForDB(instance.timestamp),
                "workflow" to writeJson(instance.workflow),
                "event" to writeJson(instance.event),
            )
        )
        instance.nodesExecutions.forEach { nx ->
            namedParameterJdbcTemplate!!.update(
                """
                   INSERT INTO WKF_INSTANCE_NODES(INSTANCE_ID, NODE_ID, STATUS, START_TIME, END_TIME, OUTPUT, ERROR) 
                   VALUES (:instanceId, :nodeId, :status, :startTime, :endTime, CAST(:output as JSONB), :error)
                """.trimIndent(),
                mapOf(
                    "instanceId" to instance.id,
                    "nodeId" to nx.id,
                    "status" to nx.status.name,
                    "startTime" to dateTimeForDB(nx.startTime),
                    "endTime" to dateTimeForDB(nx.endTime),
                    "output" to writeJson(nx.output),
                    "error" to nx.error,
                )
            )
        }
    }

    fun findWorkflowInstance(id: String): WorkflowInstance? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM WKF_INSTANCES
                WHERE ID = :id
            """.trimIndent(),
            mapOf("id" to id)
        ) { rs, _ ->
            toWorkflowInstance(rs)
        }.firstOrNull()

    private fun toWorkflowInstance(rs: ResultSet): WorkflowInstance {
        val instanceId = rs.getString("ID")
        val nodesExecutions = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM WKF_INSTANCE_NODES
                WHERE INSTANCE_ID = :instanceId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
            )
        ) { rsn, _ ->
            toWorkflowInstanceNode(rsn)
        }
        return WorkflowInstance(
            id = instanceId,
            timestamp = dateTimeFromDB(rs.getString("TIMESTAMP"))!!,
            workflow = readJson(rs, "WORKFLOW").parse(),
            event = readJson(rs, "EVENT").parse(),
            nodesExecutions = nodesExecutions,
        )
    }

    private fun toWorkflowInstanceNode(rsn: ResultSet) = WorkflowInstanceNode(
        id = rsn.getString("NODE_ID"),
        status = WorkflowInstanceNodeStatus.valueOf(rsn.getString("STATUS")),
        startTime = dateTimeFromDB(rsn.getString("START_TIME")),
        endTime = dateTimeFromDB(rsn.getString("END_TIME")),
        output = readJson(rsn, "OUTPUT"),
        error = rsn.getString("ERROR"),
    )

    fun nodeWaiting(instanceId: String, nodeId: String) {
        nodeStatus(instanceId, nodeId, WorkflowInstanceNodeStatus.WAITING)
    }

    fun nodeStarted(instanceId: String, nodeId: String) {
        nodeStatus(instanceId, nodeId, WorkflowInstanceNodeStatus.STARTED)
    }

    private fun nodeStatus(instanceId: String, nodeId: String, status: WorkflowInstanceNodeStatus) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to status.name,
            )
        )
    }

    fun nodeSuccess(instanceId: String, nodeId: String, output: JsonNode?) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, OUTPUT = CAST(:output as JSONB)
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.SUCCESS.name,
                "output" to writeJson(output),
            )
        )
    }

    fun nodeProgress(instanceId: String, nodeId: String, output: JsonNode?) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET OUTPUT = CAST(:output as JSONB)
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "output" to writeJson(output),
            )
        )
    }

    fun nodeError(instanceId: String, nodeId: String, message: String?, output: JsonNode?) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, OUTPUT = CAST(:output as JSONB), ERROR = :error
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.SUCCESS.name,
                "output" to writeJson(output),
                "error" to message,
            )
        )
    }

    fun nodeCancelled(instanceId: String, nodeId: String, message: String) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, ERROR = :error
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.CANCELLED.name,
                "error" to message,
            )
        )
    }

    fun getNodeStatus(instanceId: String, nodeId: String) =
        namedParameterJdbcTemplate!!.queryForObject(
            """
                SELECT STATUS
                FROM WKF_INSTANCE_NODES
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
            ),
            String::class.java
        )?.let { WorkflowInstanceNodeStatus.valueOf(it) }

    fun stopInstance(instanceId: String) {
        findWorkflowInstance(instanceId)?.let { instance ->
            // Marking each unfinished node as cancelled
            instance.nodesExecutions.forEach { nx ->
                if (!nx.status.finished) {
                    nodeCancelled(instanceId, nx.id, "Instance stopped")
                }
            }
        }
    }

}