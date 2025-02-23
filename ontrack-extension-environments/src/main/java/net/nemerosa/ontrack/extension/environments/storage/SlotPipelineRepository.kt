package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Build
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

    fun savePipeline(pipeline: SlotPipeline): SlotPipeline {
        // Getting the pipeline
        val existing = findPipelineById(pipeline.id)
        return if (existing != null) {
            // Saving the pipeline
            namedParameterJdbcTemplate!!.update(
                """
                UPDATE ENV_SLOT_PIPELINE
                SET "END" = :end, STATUS = :status
                WHERE ID = :id
            """,
                mapOf(
                    "id" to pipeline.id,
                    "end" to pipeline.end?.let { Time.store(it) },
                    "status" to pipeline.status.name,
                )
            )
            // OK
            pipeline
        } else {
            // Getting the number of pipelines in the slot
            val number = (namedParameterJdbcTemplate!!.queryForObject(
                """
                SELECT MAX(NUMBER)
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
            """.trimIndent(),
                mapOf("slotId" to pipeline.slot.id),
                Int::class.java,
            ) ?: 0) + 1
            // Saving the pipeline
            namedParameterJdbcTemplate!!.update(
                """
                INSERT INTO ENV_SLOT_PIPELINE (ID, SLOT_ID, BUILD_ID, NUMBER, START, "END", STATUS)
                VALUES (:id, :slotId, :buildId, :number, :start, :end, :status)
            """,
                mapOf(
                    "id" to pipeline.id,
                    "slotId" to pipeline.slot.id,
                    "buildId" to pipeline.build.id(),
                    "number" to number,
                    "start" to dateTimeForDB(pipeline.start),
                    "end" to pipeline.end?.let { Time.store(it) },
                    "status" to pipeline.status.name,
                )
            )
            // OK
            pipeline.withNumber(number)
        }
    }

    fun forAllActivePipelines(slot: Slot, code: (pipeline: SlotPipeline) -> Unit) {
        val activeStatuses = SlotPipelineStatus.activeStatuses
            .joinToString(", ") { "'$it'" }
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
                AND STATUS IN ($activeStatuses)
            """.trimIndent(),
            mapOf(
                "slotId" to slot.id,
            )
        ) { rs ->
            val pipeline = toPipeline(rs)
            code(pipeline)
        }
    }

    fun findLastPipelineBySlotAndStatusExcludingOne(
        slot: Slot,
        status: SlotPipelineStatus,
        excludedPipeline: SlotPipeline,
    ): SlotPipeline? {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
                AND ID <> :excludedPipelineId
                AND STATUS = :status
                ORDER BY NUMBER DESC
            """.trimIndent(),
            mapOf(
                "slotId" to slot.id,
                "status" to status.name,
                "excludedPipelineId" to excludedPipeline.id,
            )
        ) { rs, _ ->
            toPipeline(rs)
        }.firstOrNull()
    }

    fun findPipelines(slot: Slot, offset: Int, size: Int): PaginatedList<SlotPipeline> {
        val count = namedParameterJdbcTemplate!!.queryForObject(
            """
                SELECT COUNT(*)
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
            """.trimIndent(),
            mapOf(
                "slotId" to slot.id,
            ),
            Int::class.java
        ) ?: 0
        val list = namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
                ORDER BY NUMBER DESC
                LIMIT :size
                OFFSET :offset
            """.trimIndent(),
            mapOf(
                "slotId" to slot.id,
                "offset" to offset,
                "size" to size,
            )
        ) { rs, _ ->
            toPipeline(rs)
        }
        return PaginatedList.create(items = list, offset = offset, pageSize = size, total = count)
    }

    private fun toPipeline(rs: ResultSet) = SlotPipeline(
        id = rs.getString("ID"),
        start = Time.fromStorage(rs.getString("START"))!!,
        end = Time.fromStorage(rs.getString("END")),
        number = rs.getInt("NUMBER"),
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

    fun findLastDeployedPipeline(slot: Slot): SlotPipeline? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = :slotId
                AND STATUS = '${SlotPipelineStatus.DONE}'
                ORDER BY NUMBER DESC
                LIMIT 1
            """.trimIndent(),
            mapOf("slotId" to slot.id)
        ) { rs, _ ->
            toPipeline(rs)
        }.firstOrNull()

    fun findPipelineByBuild(build: Build): List<SlotPipeline> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM ENV_SLOT_PIPELINE
                WHERE BUILD_ID = :buildId
                ORDER BY START DESC
            """.trimIndent(),
            mapOf("buildId" to build.id())
        ) { rs, _ ->
            toPipeline(rs)
        }

    fun deleteDeployment(id: String) {
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM ENV_SLOT_PIPELINE
                WHERE ID = :id
            """.trimIndent(),
            mapOf("id" to id)
        )
    }

}