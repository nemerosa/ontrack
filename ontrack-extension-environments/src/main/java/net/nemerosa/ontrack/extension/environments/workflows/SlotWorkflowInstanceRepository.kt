package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineRepository
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStore
import net.nemerosa.ontrack.extension.workflows.engine.getById
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotWorkflowInstanceRepository(
    dataSource: DataSource,
    private val slotPipelineRepository: SlotPipelineRepository,
    private val slotWorkflowRepository: SlotWorkflowRepository,
    private val workflowInstanceStore: WorkflowInstanceStore,
) : AbstractJdbcRepository(dataSource) {

    fun addSlotWorkflowInstance(slotWorkflowInstance: SlotWorkflowInstance) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOT_WORKFLOW_INSTANCES(ID, START, PIPELINE_ID, SLOT_WORKFLOW_ID, WORKFLOW_INSTANCE_ID)
                 VALUES (:id, :start, :pipelineId, :slotWorkflowId, :workflowInstanceId)
            """,
            mapOf(
                "id" to slotWorkflowInstance.id,
                "start" to dateTimeForDB(slotWorkflowInstance.start),
                "pipelineId" to slotWorkflowInstance.pipeline.id,
                "slotWorkflowId" to slotWorkflowInstance.slotWorkflow.id,
                "workflowInstanceId" to slotWorkflowInstance.workflowInstance.id,
            )
        )
    }

    fun getSlotWorkflowInstancesByPipeline(
        pipeline: SlotPipeline,
    ): List<SlotWorkflowInstance> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_WORKFLOW_INSTANCES
                WHERE PIPELINE_ID = :pipelineId
                ORDER BY START DESC
            """,
            mapOf(
                "pipelineId" to pipeline.id
            )
        ) { rs, _ ->
            toSlotWorkflowInstance(rs)
        }

    fun findSlotWorkflowInstancesById(id: String): SlotWorkflowInstance? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_WORKFLOW_INSTANCES
                WHERE ID = :id
            """,
            mapOf(
                "id" to id
            )
        ) { rs, _ ->
            toSlotWorkflowInstance(rs)
        }.firstOrNull()

    private fun toSlotWorkflowInstance(rs: ResultSet) = SlotWorkflowInstance(
        id = rs.getString("id"),
        start = dateTimeFromDB(rs.getString("start"))!!,
        pipeline = slotPipelineRepository.getPipelineById(rs.getString("pipeline_id")),
        slotWorkflow = slotWorkflowRepository.getSlotWorkflowById(rs.getString("slot_workflow_id")),
        workflowInstance = workflowInstanceStore.getById(rs.getString("workflow_instance_id")),
    )

    fun findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow
    ): SlotWorkflowInstance? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_WORKFLOW_INSTANCES
                WHERE PIPELINE_ID = :pipelineId
                AND SLOT_WORKFLOW_ID = :slotWorkflowId
            """,
            mapOf(
                "pipelineId" to pipeline.id,
                "slotWorkflowId" to slotWorkflow.id,
            )
        ) { rs, _ ->
            toSlotWorkflowInstance(rs)
        }.firstOrNull()

}