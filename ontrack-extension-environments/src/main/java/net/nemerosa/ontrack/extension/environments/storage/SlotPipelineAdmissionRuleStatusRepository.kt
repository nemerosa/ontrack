package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineAdmissionRuleStatus
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotPipelineAdmissionRuleStatusRepository(
    dataSource: DataSource,
    private val slotAdmissionRuleConfigRepository: SlotAdmissionRuleConfigRepository,
) : AbstractJdbcRepository(dataSource) {

    fun saveStatus(slotPipelineAdmissionRuleStatus: SlotPipelineAdmissionRuleStatus) {
        namedParameterJdbcTemplate!!.update(
            """
                INSERT INTO ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS(ID, PIPELINE_ID, ADMISSION_RULE_CONFIG_ID, "USER", TIMESTAMP, DATA, OVERRIDE, OVERRIDE_MESSAGE)
                VALUES(:id, :pipelineId, :admissionRuleConfigId, :user, :timestamp, CAST(:data AS JSONB), :override, :overrideMessage)
            """.trimIndent(),
            mapOf(
                "id" to slotPipelineAdmissionRuleStatus.id,
                "pipelineId" to slotPipelineAdmissionRuleStatus.pipeline.id,
                "admissionRuleConfigId" to slotPipelineAdmissionRuleStatus.admissionRuleConfig.id,
                "user" to slotPipelineAdmissionRuleStatus.user,
                "timestamp" to Time.store(slotPipelineAdmissionRuleStatus.timestamp),
                "data" to slotPipelineAdmissionRuleStatus.data?.let { writeJson(it) },
                "override" to slotPipelineAdmissionRuleStatus.override,
                "overrideMessage" to slotPipelineAdmissionRuleStatus.overrideMessage,
            )
        )
    }

    fun findStatusesByPipelineAndAdmissionRuleConfig(
        pipeline: SlotPipeline,
        config: SlotAdmissionRuleConfig
    ): List<SlotPipelineAdmissionRuleStatus> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM env_slot_pipeline_admission_rule_status
                WHERE pipeline_id = :pipelineId
                AND admission_rule_config_id = :admissionRuleConfigId
            """.trimIndent(),
            mapOf(
                "pipelineId" to pipeline.id,
                "admissionRuleConfigId" to config.id,
            )
        ) { rs, _ ->
            toSlotPipelineAdmissionRuleStatus(rs, pipeline, config)
        }

    fun findStatusesByPipeline(pipeline: SlotPipeline): List<SlotPipelineAdmissionRuleStatus> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM env_slot_pipeline_admission_rule_status
                WHERE pipeline_id = :pipelineId
            """.trimIndent(),
            mapOf(
                "pipelineId" to pipeline.id,
            )
        ) { rs, _ ->
            toSlotPipelineAdmissionRuleStatus(rs, pipeline)
        }

    private fun toSlotPipelineAdmissionRuleStatus(
        rs: ResultSet,
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig? = null,
    ) = SlotPipelineAdmissionRuleStatus(
        id = rs.getString("id"),
        pipeline = pipeline,
        admissionRuleConfig = admissionRuleConfig ?: slotAdmissionRuleConfigRepository.getAdmissionRuleConfigById(
            pipeline.slot,
            rs.getString("ADMISSION_RULE_CONFIG_ID")
        ),
        override = rs.getBoolean("override"),
        overrideMessage = rs.getString("override_message"),
        user = rs.getString("user"),
        timestamp = Time.fromStorage(rs.getString("timestamp"))!!,
        data = readJson(rs, "data"),
    )

}