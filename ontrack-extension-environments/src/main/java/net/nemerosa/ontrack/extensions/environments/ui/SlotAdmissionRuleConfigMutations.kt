package net.nemerosa.ontrack.extensions.environments.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class SlotAdmissionRuleConfigMutations(
    private val slotService: SlotService,
    private val slotAdmissionRuleRegistry: SlotAdmissionRuleRegistry,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "saveSlotAdmissionRuleConfig",
            description = "Saves or creates a configured admission rule for a slot",
            input = SaveSlotAdmissionRuleConfigInput::class,
            outputName = "admissionRuleConfig",
            outputDescription = "Saved admission rule config",
            outputType = SlotAdmissionRuleConfig::class,
        ) { input ->
            if (!input.id.isNullOrBlank()) {
                if (!input.slotId.isNullOrBlank()) {
                    error("If ID is provided, the ID of slot is not needed.")
                } else {
                    TODO("Saving the configuration of an existing rule")
                }
            } else if (!input.slotId.isNullOrBlank()) {
                val slot = slotService.getSlotById(input.slotId)
                // Getting the rule
                val rule = slotAdmissionRuleRegistry.getRule(input.ruleId)
                // Checking the configuration
                rule.checkConfig(input.ruleConfig)
                // Creation of a new rule
                val config = SlotAdmissionRuleConfig(
                    slot = slot,
                    name = input.name?.takeIf { it.isNotBlank() } ?: rule.name,
                    description = input.description,
                    ruleId = input.ruleId,
                    ruleConfig = input.ruleConfig,
                )
                slotService.addAdmissionRuleConfig(
                    slot = slot,
                    config = config
                )
                config
            } else {
                error("Either the ID or the slot ID must be provided.")
            }
        },
        unitMutation(
            name = "deleteSlotAdmissionRuleConfig",
            description = "Deletes an admission rule for a slot",
            input = DeleteSlotAdmissionRuleConfigInput::class,
        ) { input ->
            val config = slotService.findAdmissionRuleConfigById(input.id)
            if (config != null) {
                slotService.deleteAdmissionRuleConfig(config)
            }
        },
    )
}

data class SaveSlotAdmissionRuleConfigInput(
    val id: String?,
    val slotId: String?,
    val name: String?,
    val description: String,
    val ruleId: String,
    val ruleConfig: JsonNode,
)

data class DeleteSlotAdmissionRuleConfigInput(
    val id: String,
)