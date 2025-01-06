package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.*
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
                INSERT INTO ENV_SLOT_PIPELINE_ADMISSION_RULE_STATUS(ID, PIPELINE_ID, ADMISSION_RULE_CONFIG_ID, DATA, DATA_USER, DATA_TIMESTAMP, OVERRIDE_USER, OVERRIDE_TIMESTAMP, OVERRIDE_MESSAGE)
                VALUES(:id, :pipelineId, :admissionRuleConfigId, CAST(:data AS JSONB), :dataUser, :dataTimestamp, :overrideUser, :overrideTimestamp, :overrideMessage)
            """.trimIndent(),
            mapOf(
                "id" to slotPipelineAdmissionRuleStatus.id,
                "pipelineId" to slotPipelineAdmissionRuleStatus.pipeline.id,
                "admissionRuleConfigId" to slotPipelineAdmissionRuleStatus.admissionRuleConfig.id,
                "data" to slotPipelineAdmissionRuleStatus.data?.data?.let { writeJson(it) },
                "dataUser" to slotPipelineAdmissionRuleStatus.data?.user,
                "dataTimestamp" to slotPipelineAdmissionRuleStatus.data?.timestamp?.let { Time.store(it) },
                "overrideUser" to slotPipelineAdmissionRuleStatus.override?.user,
                "overrideTimestamp" to slotPipelineAdmissionRuleStatus.override?.timestamp?.let { Time.store(it) },
                "overrideMessage" to slotPipelineAdmissionRuleStatus.override?.message,
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
        data = readJson(rs, "data")?.let { data ->
            SlotAdmissionRuleData(
                user = rs.getString("data_user"),
                timestamp = dateTimeFromDB(rs.getString("data_timestamp"))!!,
                data = data,
            )
        },
        override = rs.getString("override_user")?.let { user ->
            SlotAdmissionRuleOverride(
                user = user,
                timestamp = dateTimeFromDB(rs.getString("override_timestamp"))!!,
                message = rs.getString("override_message"),
            )
        },
    )

}