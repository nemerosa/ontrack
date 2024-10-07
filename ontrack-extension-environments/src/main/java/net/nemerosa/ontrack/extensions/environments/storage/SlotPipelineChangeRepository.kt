package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extensions.environments.SlotPipeline
import net.nemerosa.ontrack.extensions.environments.SlotPipelineChange
import net.nemerosa.ontrack.extensions.environments.SlotPipelineStatus
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
                INSERT INTO ENV_SLOT_PIPELINE_CHANGE (ID, PIPELINE_ID, "USER", TIMESTAMP, STATUS, MESSAGE)
                VALUES (:id, :pipelineId, :user, :timestamp, :status, :message)
            """,
            mapOf(
                "id" to slotPipelineChange.id,
                "pipelineId" to slotPipelineChange.pipeline.id,
                "user" to slotPipelineChange.user,
                "timestamp" to Time.store(slotPipelineChange.timestamp),
                "status" to slotPipelineChange.status?.name,
                "message" to slotPipelineChange.message,
            )
        )
    }

    fun findByPipeline(pipeline: SlotPipeline): List<SlotPipelineChange> {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE_CHANGE
                WHERE PIPELINE_ID = :pipelineId
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
                status = rs.getString("status")?.let { SlotPipelineStatus.valueOf(it) },
                message = rs.getString("message"),
            )
        }
    }

}