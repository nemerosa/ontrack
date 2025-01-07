package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineChange
import net.nemerosa.ontrack.extension.environments.SlotPipelineChangeType
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class SlotPipelineChangeRepository(
    dataSource: DataSource,
    private val slotPipelineRepository: SlotPipelineRepository,
) : AbstractJdbcRepository(dataSource) {

    fun save(slotPipelineChange: SlotPipelineChange) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOT_PIPELINE_CHANGE (ID, PIPELINE_ID, "USER", TIMESTAMP, TYPE, STATUS, MESSAGE, OVERRIDE_MESSAGE)
                VALUES (:id, :pipelineId, :user, :timestamp, :type, :status, :message, :overrideMessage)
            """,
            mapOf(
                "id" to slotPipelineChange.id,
                "pipelineId" to slotPipelineChange.pipeline.id,
                "user" to slotPipelineChange.user,
                "timestamp" to Time.store(slotPipelineChange.timestamp),
                "type" to slotPipelineChange.type.name,
                "status" to slotPipelineChange.status?.name,
                "message" to slotPipelineChange.message,
                "overrideMessage" to slotPipelineChange.overrideMessage,
            )
        )
    }

    fun findByPipeline(pipeline: SlotPipeline): List<SlotPipelineChange> {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE_CHANGE
                WHERE PIPELINE_ID = :pipelineId
                ORDER BY TIMESTAMP DESC
            """.trimIndent(),
            mapOf(
                "pipelineId" to pipeline.id,
            )
        ) { rs, _ ->
            SlotPipelineChange(
                id = rs.getString("id"),
                pipeline = slotPipelineRepository.getPipelineById(rs.getString("pipeline_id")),
                user = rs.getString("user"),
                timestamp = Time.fromStorage(rs.getString("timestamp"))!!,
                type = SlotPipelineChangeType.valueOf(rs.getString("type")),
                status = rs.getString("status")?.let { SlotPipelineStatus.valueOf(it) },
                message = rs.getString("message"),
                overrideMessage = rs.getString("override_message"),
            )
        }
    }

}