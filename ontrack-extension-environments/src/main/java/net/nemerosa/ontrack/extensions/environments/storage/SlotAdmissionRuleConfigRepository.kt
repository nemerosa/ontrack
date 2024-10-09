package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.service.SlotAdmissionRuleConfigIdNotFound
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class SlotAdmissionRuleConfigRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {

    fun addAdmissionRuleConfig(slot: Slot, config: SlotAdmissionRuleConfig) {
        namedParameterJdbcTemplate!!.update(
            """
                 INSERT INTO ENV_SLOT_ADMISSION_RULE_CONFIGS(ID, SLOT_ID, NAME, DESCRIPTION, RULE_ID, RULE_CONFIG)
                 VALUES (:id, :slotId, :name, :description, :ruleId, CAST(:ruleConfig AS JSONB))
            """,
            mapOf(
                "id" to config.id,
                "slotId" to slot.id,
                "name" to config.name,
                "description" to config.description,
                "ruleId" to config.ruleId,
                "ruleConfig" to writeJson(config.ruleConfig),
            )
        )
    }

    fun getAdmissionRuleConfigs(slot: Slot): List<SlotAdmissionRuleConfig> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                    FROM ENV_SLOT_ADMISSION_RULE_CONFIGS
                    WHERE SLOT_ID = :slotId
                    ORDER BY NAME
            """,
            mapOf(
                "slotId" to slot.id,
            )
        ) { rs, _ ->
            toSlotAdmissionRuleConfig(rs)
        }

    private fun toSlotAdmissionRuleConfig(rs: ResultSet) = SlotAdmissionRuleConfig(
        id = rs.getString("id"),
        name = rs.getString("name"),
        description = rs.getString("description"),
        ruleId = rs.getString("rule_id"),
        ruleConfig = readJson(rs, "rule_config")
    )

    fun deleteAdmissionRuleConfig(config: SlotAdmissionRuleConfig) {
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM ENV_SLOT_ADMISSION_RULE_CONFIGS
                 WHERE ID = :id
            """,
            mapOf("id" to config.id)
        )
    }

    fun getAdmissionRuleConfigById(slot: Slot, id: String): SlotAdmissionRuleConfig =
        namedParameterJdbcTemplate!!.queryForObject(
            """
                SELECT *
                    FROM ENV_SLOT_ADMISSION_RULE_CONFIGS
                    WHERE SLOT_ID = :slotId
                    AND ID = :id
            """,
            mapOf(
                "slotId" to slot.id,
                "id" to id,
            )
        ) { rs, _ ->
            toSlotAdmissionRuleConfig(rs)
        } ?: throw SlotAdmissionRuleConfigIdNotFound(id)

}