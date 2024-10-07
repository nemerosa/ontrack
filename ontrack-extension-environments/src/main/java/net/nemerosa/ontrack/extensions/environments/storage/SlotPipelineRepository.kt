package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotPipeline
import net.nemerosa.ontrack.extensions.environments.SlotPipelineStatus
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.repository.BuildJdbcRepositoryAccessor
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotPipelineRepository(
    dataSource: DataSource,
    private val buildJdbcRepositoryAccessor: BuildJdbcRepositoryAccessor,
    private val slotRepository: SlotRepository,
) : AbstractJdbcRepository(dataSource) {

    fun savePipeline(pipeline: SlotPipeline) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOT_PIPELINE (ID, SLOT_ID, BUILD_ID, START, "END", STATUS)
                VALUES (:id, :slotId, :buildId, :start, :end, :status)
                ON CONFLICT (ID) DO UPDATE SET
                "END" = excluded."END",
                STATUS = excluded.STATUS
            """,
            mapOf(
                "id" to pipeline.id,
                "slotId" to pipeline.slot.id,
                "buildId" to pipeline.build.id(),
                "start" to dateTimeForDB(pipeline.start),
                "end" to pipeline.end?.let { Time.store(it) },
                "status" to pipeline.status.name,
            )
        )
    }

    fun findPipelines(slot: Slot): PaginatedList<SlotPipeline> {
        // TODO Pagination
        val list = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
                ORDER BY START DESC
            """.trimIndent(),
            mapOf(
                "slotId" to slot.id,
            )
        ) { rs, _ ->
            toPipeline(rs)
        }
        return PaginatedList.create(list, 0, 10)
    }

    private fun toPipeline(rs: ResultSet) = SlotPipeline(
        id = rs.getString("ID"),
        start = Time.fromStorage(rs.getString("START"))!!,
        end = Time.fromStorage(rs.getString("END")),
        status = SlotPipelineStatus.valueOf(rs.getString("STATUS")),
        build = buildJdbcRepositoryAccessor.getBuild(id(rs, "BUILD_ID")),
        slot = slotRepository.getSlotById(rs.getString("SLOT_ID")),
    )

    fun findPipelineById(id: String): SlotPipeline? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                 FROM env_slot_pipeline
                 WHERE id = :id
            """.trimIndent(),
            mapOf("id" to id)
        ) { rs, _ ->
            toPipeline(rs)
        }.firstOrNull()

    fun getPipelineById(id: String): SlotPipeline =
        findPipelineById(id) ?: throw SlotPipelineIdNotFoundException(id)

}