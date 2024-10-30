package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class ManualInputSlotAdmissionRule :
    SlotAdmissionRule<ManualInputSlotAdmissionRuleConfig, ManualInputSlotAdmissionRuleData> {

    companion object {
        const val ID = "manualInput"
    }

    override val id: String = ID
    override val name: String = "Manual input"

    override fun parseConfig(jsonRuleConfig: JsonNode): ManualInputSlotAdmissionRuleConfig = jsonRuleConfig.parse()

    override fun parseData(node: JsonNode): ManualInputSlotAdmissionRuleData = node.parse()

    override fun checkConfig(ruleConfig: JsonNode) {
        ruleConfig.parseOrNull<ManualInputSlotAdmissionRuleConfig>()
            ?: throw SlotAdmissionRuleConfigException("Cannot parse the rule config")
    }

    /**
     * Any build is eligible. Input is required only when reaching deployment checks.
     */
    override fun isBuildEligible(build: Build, slot: Slot, config: ManualInputSlotAdmissionRuleConfig): Boolean = true


    override fun fillEligibilityCriteria(
        slot: Slot,
        config: ManualInputSlotAdmissionRuleConfig,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>
    ) {
    }

    override fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: ManualInputSlotAdmissionRuleConfig,
        ruleData: SlotPipelineAdmissionRuleData<ManualInputSlotAdmissionRuleData>?
    ): DeployableCheck {
        // Data is required
        if (ruleData?.data == null) {
            return DeployableCheck.nok("Manual input is required")
        }
        // Checking each field
        ruleConfig.fields.forEach { field ->
            val data = ruleData.data.findFieldValue(field.name)
            val validation = field.type.validate(data)
            if (!validation.ok) {
                val message = if (validation.message.isNullOrBlank()) {
                    "${field.name} input is missing or invalid"
                } else {
                    "${field.name} input is invalid: ${validation.message}"
                }
                return DeployableCheck.nok(message)
            }
        }
        // OK
        return DeployableCheck.ok()
    }
}