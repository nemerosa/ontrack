package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.storage.SlotRepository
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotWorkflowRepository(
    dataSource: DataSource,
    private val slotRepository: SlotRepository,
) : AbstractJdbcRepository(dataSource) {

    fun addSlotWorkflow(slotWorkflow: SlotWorkflow) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOT_WORKFLOWS(ID, SLOT_ID, TRIGGER, WORKFLOW)
                 VALUES (:id, :slotId, :trigger, CAST(:workflow AS JSONB))
                ON CONFLICT (ID) DO UPDATE SET
                    TRIGGER = :trigger,
                    WORKFLOW = CAST(:workflow AS JSONB)
            """,
            mapOf(
                "id" to slotWorkflow.id,
                "slotId" to slotWorkflow.slot.id,
                "trigger" to slotWorkflow.trigger.name,
                "workflow" to writeJson(slotWorkflow.workflow),
            )
        )
    }

    fun deleteSlotWorkflow(slotWorkflow: SlotWorkflow) {
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM ENV_SLOT_WORKFLOWS
                WHERE ID = :id
            """,
            mapOf("id" to slotWorkflow.id)
        )
    }

    fun updateSlotWorkflow(slotWorkflow: SlotWorkflow) {
        namedParameterJdbcTemplate!!.update(
            """
                UPDATE ENV_SLOT_WORKFLOWS
                SET TRIGGER = :trigger, WORKFLOW = CAST(:workflow AS JSONB)
                WHERE ID = :id
            """.trimIndent(),
            mapOf(
                "id" to slotWorkflow.id,
                "trigger" to slotWorkflow.trigger.name,
                "workflow" to writeJson(slotWorkflow.workflow),
            )
        )
    }

    fun getSlotWorkflowsBySlotAndTrigger(slot: Slot, trigger: SlotWorkflowTrigger): List<SlotWorkflow> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_WORKFLOWS
                WHERE SLOT_ID = :slotId
                AND TRIGGER = :trigger
            """,
            mapOf(
                "slotId" to slot.id,
                "trigger" to trigger.name,
            )
        ) { rs, _ ->
            toSlotWorkflow(rs)
        }

    fun getSlotWorkflowsBySlot(slot: Slot): List<SlotWorkflow> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_WORKFLOWS
                WHERE SLOT_ID = :slotId
            """,
            mapOf(
                "slotId" to slot.id,
            )
        ) { rs, _ ->
            toSlotWorkflow(rs)
        }

    private fun toSlotWorkflow(rs: ResultSet) = SlotWorkflow(
        id = rs.getString("id"),
        slot = slotRepository.getSlotById(rs.getString("slot_id")),
        trigger = SlotWorkflowTrigger.valueOf(rs.getString("trigger")),
        workflow = readJson(rs, "workflow").parse(),
    )

    fun findSlotWorkflowById(id: String): SlotWorkflow? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                 FROM ENV_SLOT_WORKFLOWS
                 WHERE ID = :id
            """.trimIndent(),
            mapOf("id" to id)
        ) { rs, _ ->
            toSlotWorkflow(rs)
        }.firstOrNull()

    fun getSlotWorkflowById(id: String) =
        findSlotWorkflowById(id) ?: throw SlotWorkflowIdNotFoundException(id)

}