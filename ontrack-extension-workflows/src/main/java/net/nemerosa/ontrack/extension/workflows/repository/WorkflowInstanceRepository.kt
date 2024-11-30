package net.nemerosa.ontrack.extension.workflows.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceFilter
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.events.merge
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
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
                "status" to WorkflowInstanceNodeStatus.WAITING.name,
            )
        )
    }

    fun nodeStarted(instanceId: String, nodeId: String) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, START_TIME = :startTime
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.STARTED.name,
                "startTime" to dateTimeForDB(Time.now),
            )
        )
    }

    fun nodeSuccess(instanceId: String, nodeId: String, output: JsonNode?, event: SerializableEvent?) {
        if (event != null) {
            mergeInstanceEvent(instanceId, event)
        }
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, OUTPUT = CAST(:output as JSONB), END_TIME = :endTime
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.SUCCESS.name,
                "output" to writeJson(output),
                "endTime" to dateTimeForDB(Time.now),
            )
        )
    }

    private fun mergeInstanceEvent(instanceId: String, event: SerializableEvent) {
        // Locking the existing event
        val existingEvent = namedParameterJdbcTemplate!!.query(
            """
                SELECT EVENT
                FROM WKF_INSTANCES
                WHERE ID = :instanceId
                FOR UPDATE
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
            )
        ) { rs, _ ->
            readJson(rs, "EVENT").parse<SerializableEvent>()
        }.first()
        // Merging the two events
        val mergedEvent: SerializableEvent = existingEvent.merge(event)
        // Saving the event back
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCES
                SET EVENT = CAST(:event AS JSONB)
                WHERE ID = :instanceId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "event" to writeJson(mergedEvent)
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
        val actualOutput = output ?: getNodeOutput(instanceId, nodeId)
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, OUTPUT = CAST(:output as JSONB), ERROR = :error, END_TIME = :endTime
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.ERROR.name,
                "output" to writeJson(actualOutput),
                "error" to message,
                "endTime" to dateTimeForDB(Time.now),
            )
        )
    }

    fun nodeCancelled(instanceId: String, nodeId: String, message: String) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCE_NODES
                SET STATUS = :status, ERROR = :error, END_TIME = :endTime
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
                "status" to WorkflowInstanceNodeStatus.CANCELLED.name,
                "error" to message,
                "endTime" to dateTimeForDB(Time.now),
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

    fun getNodeOutput(instanceId: String, nodeId: String): JsonNode? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT OUTPUT
                FROM WKF_INSTANCE_NODES
                WHERE INSTANCE_ID = :instanceId
                AND NODE_ID = :nodeId
            """.trimIndent(),
            mapOf(
                "instanceId" to instanceId,
                "nodeId" to nodeId,
            ),
        ) { rs, _ ->
            readJson(rs, "OUTPUT")
        }.firstOrNull()

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

    fun findInstances(workflowInstanceFilter: WorkflowInstanceFilter): PaginatedList<WorkflowInstance> {

        val criterias = mutableListOf<String>()
        val params = mutableMapOf<String, Any?>()

        if (!workflowInstanceFilter.name.isNullOrBlank()) {
            criterias += "WORKFLOW::JSONB->>'name' = :name"
            params["name"] = workflowInstanceFilter.name
        }

        val where = if (criterias.isEmpty()) {
            ""
        } else {
            "WHERE " + criterias.joinToString(" AND ") { "($it)" }
        }

        val count = namedParameterJdbcTemplate!!.queryForObject(
            """
                SELECT COUNT(*)
                FROM WKF_INSTANCES
                $where
            """.trimIndent(),
            params,
            Int::class.java
        ) ?: 0

        val items = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM WKF_INSTANCES
                $where
                LIMIT :limit
                OFFSET :offset
            """.trimIndent(),
            params + mapOf(
                "limit" to workflowInstanceFilter.size,
                "offset" to workflowInstanceFilter.offset
            )
        ) { rs, _ ->
            toWorkflowInstance(rs)
        }

        return PaginatedList.create(
            items = items,
            offset = workflowInstanceFilter.offset,
            pageSize = workflowInstanceFilter.size,
            total = count
        )
    }

    fun clearAll() {
        jdbcTemplate!!.update(
            """
                -- noinspection SqlWithoutWhere
                DELETE FROM WKF_INSTANCES
            """.trimIndent()
        )
    }

    fun cleanup(time: LocalDateTime) {
        val timestamp = dateTimeForDB(time)!!
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM WKF_INSTANCES
                WHERE TIMESTAMP < :timestamp
            """.trimIndent(),
            mapOf(
                "timestamp" to timestamp,
            )
        )
    }

}