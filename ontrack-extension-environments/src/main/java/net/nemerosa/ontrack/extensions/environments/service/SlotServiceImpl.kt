package net.nemerosa.ontrack.extensions.environments.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extensions.environments.storage.SlotAdmissionRuleConfigRepository
import net.nemerosa.ontrack.extensions.environments.storage.SlotAlreadyDefinedException
import net.nemerosa.ontrack.extensions.environments.storage.SlotIdAlreadyExistsException
import net.nemerosa.ontrack.extensions.environments.storage.SlotRepository
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SlotServiceImpl(
    private val slotRepository: SlotRepository,
    private val slotAdmissionRuleConfigRepository: SlotAdmissionRuleConfigRepository,
    private val slotAdmissionRuleRegistry: SlotAdmissionRuleRegistry,
) : SlotService {

    override fun addSlot(slot: Slot) {
        // TODO Security check
        // Checks for ID
        if (slotRepository.findSlotById(slot.id) != null) {
            throw SlotIdAlreadyExistsException(slot.id)
        }
        // Checks for project & qualifier
        val existing =
            slotRepository.findByEnvironmentAndProjectAndQualifier(slot.environment, slot.project, slot.qualifier)
        if (existing != null) {
            throw SlotAlreadyDefinedException(slot.environment, slot.project, slot.qualifier)
        }
        // Saving
        slotRepository.addSlot(slot)
    }

    override fun findSlotsByEnvironment(environment: Environment): List<Slot> {
        // TODO Checks for security
        // TODO Security filter on the slots projects
        return slotRepository.findByEnvironment(environment).sortedBy { it.project.name }
    }

    override fun addAdmissionRuleConfig(slot: Slot, config: SlotAdmissionRuleConfig) {
        // TODO Security check
        slotAdmissionRuleConfigRepository.addAdmissionRuleConfig(slot, config)
    }

    override fun getAdmissionRuleConfigs(slot: Slot): List<SlotAdmissionRuleConfig> {
        // TODO Security checks
        return slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(slot)
    }

    override fun deleteAdmissionRuleConfig(config: SlotAdmissionRuleConfig) {
        // TODO Security check
        slotAdmissionRuleConfigRepository.deleteAdmissionRuleConfig(config)
    }

    override fun isBuildEligible(slot: Slot, build: Build): Boolean {
        // TODO Security check
        // Always checking the project
        if (build.project != slot.project) return false
        // Gets all the admission rules
        val configs = slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(slot)
        // All rules must assert that the build is OK for being a candidate
        return configs.all { config ->
            isBuildEligible(slot, config, build)
        }
    }

    private fun isBuildEligible(slot: Slot, config: SlotAdmissionRuleConfig, build: Build): Boolean {
        val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
        return isBuildEligible(slot, rule, config.ruleConfig, build)
    }

    private fun <C> isBuildEligible(
        slot: Slot,
        rule: SlotAdmissionRule<C>,
        jsonRuleConfig: JsonNode,
        build: Build
    ): Boolean {
        // Parsing the config
        val ruleConfig = rule.parseConfig(jsonRuleConfig)
        // Checking the rule
        return rule.isBuildEligible(build, slot, ruleConfig)
    }

    override fun getSlotById(id: String): Slot {
        // TODO Security check
        return slotRepository.getSlotById(id)
    }
}