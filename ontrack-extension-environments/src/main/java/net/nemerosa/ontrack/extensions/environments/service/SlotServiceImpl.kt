package net.nemerosa.ontrack.extensions.environments.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.*
import net.nemerosa.ontrack.extensions.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extensions.environments.storage.*
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SlotServiceImpl(
    private val slotRepository: SlotRepository,
    private val slotAdmissionRuleConfigRepository: SlotAdmissionRuleConfigRepository,
    private val slotPipelineRepository: SlotPipelineRepository,
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

    override fun getEligibleBuilds(slot: Slot, count: Int): List<Build> {
        // TODO Security check
        // Gets all the admission rules
        val configs = slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(slot)
        // Gets all eligible builds
        return configs.flatMap { config ->
            getEligibleBuilds(slot, config, count)
        }
            .distinctBy { it.id }
            .sortedByDescending { it.signature.time }
    }

    private fun getEligibleBuilds(slot: Slot, config: SlotAdmissionRuleConfig, count: Int): List<Build> {
        val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
        return getEligibleBuilds(slot, rule, config.ruleConfig, count)
    }

    private fun <C> getEligibleBuilds(
        slot: Slot,
        rule: SlotAdmissionRule<C>,
        jsonRuleConfig: JsonNode,
        count: Int,
    ): List<Build> {
        val ruleConfig = rule.parseConfig(jsonRuleConfig)
        return rule.getEligibleBuilds(slot, ruleConfig, count)
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

    override fun startPipeline(slot: Slot, build: Build): SlotPipeline {
        // TODO Security check
        // Build must be eligible
        if (!isBuildEligible(slot, build)) {
            throw SlotPipelineBuildNotEligibleException(slot, build)
        }
        // TODO Cancelling all current pipelines
        // Creating the new pipeline
        val pipeline = SlotPipeline(build = build)
        // Saving the pipeline
        slotPipelineRepository.savePipeline(slot, pipeline)
        // OK
        return pipeline
    }

    override fun findPipelines(slot: Slot): PaginatedList<SlotPipeline> {
        // TODO Security check
        return slotPipelineRepository.findPipelines(slot)
    }
}