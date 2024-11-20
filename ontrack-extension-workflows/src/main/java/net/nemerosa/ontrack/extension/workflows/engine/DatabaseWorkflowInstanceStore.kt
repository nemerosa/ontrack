package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.mgt.WorkflowSettings
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.sql.DataSource

@Component
@Transactional
class DatabaseWorkflowInstanceStore(
    dataSource: DataSource,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractJdbcRepository(dataSource), WorkflowInstanceStore {

    override fun create(instance: WorkflowInstance): WorkflowInstance {
        namedParameterJdbcTemplate!!.update(
            """
                        INSERT INTO WKF_INSTANCES(ID, TIMESTAMP, WORKFLOW, EVENT)
                        VALUES (:id, :timestamp, CAST(:workflow AS JSONB), CAST(:event AS JSONB))
                    """,
            mapOf(
                "id" to instance.id,
                "timestamp" to dateTimeForDB(instance.timestamp),
                "workflow" to writeJson(instance.workflow),
                "event" to writeJson(instance.event),
            )
        )
        return doSaveNodes(instance)
    }

    override fun saveEvent(instance: WorkflowInstance, event: SerializableEvent): WorkflowInstance {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCES
                SET EVENT = CAST(:event AS JSONB)
                WHERE ID = :id
            """,
            mapOf(
                "id" to instance.id,
                "event" to writeJson(instance.event),
            )
        )
        return getById(instance.id)
    }

    override fun error(instance: WorkflowInstance, message: String, throwable: Exception): WorkflowInstance {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE WKF_INSTANCES
                SET ERROR = :error
                WHERE ID = :id
            """,
            mapOf(
                "id" to instance.id,
                "error" to message,
            )
        )
        return getById(instance.id)
    }

    override fun stop(instance: WorkflowInstance) {
        val stoppedInstance = instance.stopNodes()
        doSaveNodes(stoppedInstance)
    }

    private fun doSaveNodes(instance: WorkflowInstance): WorkflowInstance {
        // Saving or updating all nodes
        instance.nodesExecutions.forEach { node ->
            doSaveNode(instance, node)
        }
        // Getting the final instance
        return getById(instance)
    }

    private fun getById(instance: WorkflowInstance) =
        doFindById(instance.id)
            ?: throw WorkflowInstanceNotFoundException(instance.id)

    override fun saveNode(
        instance: WorkflowInstance,
        nodeId: String,
        nodeUpdate: (node: WorkflowInstanceNode) -> WorkflowInstanceNode
    ): WorkflowInstance {
        val node = instance.getNode(nodeId).run(nodeUpdate)
        doSaveNode(instance, node)
        return getById(instance.id)
    }

    private fun doSaveNode(instance: WorkflowInstance, node: WorkflowInstanceNode) {
        namedParameterJdbcTemplate!!.update(
            """
                    INSERT INTO WKF_INSTANCES_NODES (WKF_INSTANCES_ID, NODE_ID, STATUS, START_TIME, END_TIME, OUTPUT, ERROR)
                    VALUES (:instanceId, :nodeId, :status, :startTime, :endTime, CAST(:output as JSONB), :error)
                    ON CONFLICT (WKF_INSTANCES_ID, NODE_ID) DO UPDATE SET
                    STATUS = :status,
                    END_TIME = :endTime,
                    OUTPUT = CAST(:output as JSONB),
                    ERROR = :error
                """,
            mapOf(
                "instanceId" to instance.id,
                "nodeId" to node.id,
                "status" to node.status.name,
                "startTime" to dateTimeForDB(node.startTime),
                "endTime" to dateTimeForDB(node.endTime),
                "output" to writeJson(node.output),
                "error" to node.error,
            )
        )
    }

    override fun cleanup() {
        val settings = cachedSettingsService.getCachedSettings(WorkflowSettings::class.java)
        val time = Time.now - Duration.of(settings.retentionDuration, ChronoUnit.MILLIS)
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM WKF_INSTANCES
                 WHERE TIMESTAMP < :timestamp
            """,
            mapOf(
                "timestamp" to dateTimeForDB(time),
            )
        )
    }

    private fun doFindById(id: String): WorkflowInstance? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM WKF_INSTANCES
                WHERE ID = :id
            """,
            mapOf("id" to id)
        ) { rs, _ ->
            toWorkflowInstance(rs)
        }.firstOrNull()

    private fun toWorkflowInstance(rs: ResultSet): WorkflowInstance {
        val instanceId = rs.getString("ID")
        val nodes = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM WKF_INSTANCES_NODES
                WHERE WKF_INSTANCES_ID = :instanceId
            """,
            mapOf(
                "instanceId" to instanceId,
            )
        ) { rsn, _ ->
            WorkflowInstanceNode(
                id = rsn.getString("NODE_ID"),
                status = WorkflowInstanceNodeStatus.valueOf(rsn.getString("STATUS")),
                startTime = dateTimeFromDB(rsn.getString("START_TIME")),
                endTime = dateTimeFromDB(rsn.getString("END_TIME")),
                output = readJson(rsn, "OUTPUT"),
                error = rsn.getString("ERROR"),
            )
        }
        return WorkflowInstance(
            id = instanceId,
            timestamp = dateTimeFromDB(rs.getString("TIMESTAMP"))!!,
            workflow = readJson(rs.getString("WORKFLOW")).parse(),
            event = readJson(rs.getString("EVENT")).parse(),
            error = rs.getString("ERROR"),
            nodesExecutions = nodes,
        )
    }

    override fun findById(id: String): WorkflowInstance? =
        doFindById(id)

    override fun findByFilter(workflowInstanceFilter: WorkflowInstanceFilter): PaginatedList<WorkflowInstance> {

        val query = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, Any?>()

        if (!workflowInstanceFilter.name.isNullOrBlank()) {
            query += "WORKFLOW::JSONB->>'name' = :name"
            queryVariables["name"] = workflowInstanceFilter.name
        }

        val querySelect = query.joinToString(" ") { " AND ($it)" }

        // Count
        @Suppress("SqlSourceToSinkFlow")
        val total = namedParameterJdbcTemplate!!.queryForObject(
            """
                -- noinspection SqlConstantExpression
                SELECT COUNT(ID) FROM WKF_INSTANCES
                WHERE 1 = 1 $querySelect
            """,
            queryVariables,
            Int::class.java
        ) ?: 0

        // Page
        @Suppress("SqlSourceToSinkFlow")
        val instances = namedParameterJdbcTemplate!!.query(
            """
                -- noinspection SqlConstantExpression
                SELECT * 
                FROM WKF_INSTANCES
                WHERE 1 = 1 $querySelect
                ORDER BY TIMESTAMP DESC
                LIMIT :limit
                OFFSET :offset
            """,
            queryVariables + mapOf("limit" to workflowInstanceFilter.size, "offset" to workflowInstanceFilter.offset),
        ) { rs, _ ->
            toWorkflowInstance(rs)
        }

        // OK
        return PaginatedList.create(
            items = instances,
            offset = workflowInstanceFilter.offset,
            pageSize = workflowInstanceFilter.size,
            total = total,
        )
    }

    override fun clearAll() {
        jdbcTemplate!!.update(
            """
                -- noinspection SqlWithoutWhere
                DELETE FROM WKF_INSTANCES
            """
        )
    }
}