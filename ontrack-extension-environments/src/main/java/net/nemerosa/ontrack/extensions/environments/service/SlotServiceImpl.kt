package net.nemerosa.ontrack.extensions.environments.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extensions.environments.*
import net.nemerosa.ontrack.extensions.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extensions.environments.storage.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SlotServiceImpl(
    private val securityService: SecurityService,
    private val slotRepository: SlotRepository,
    private val slotAdmissionRuleConfigRepository: SlotAdmissionRuleConfigRepository,
    private val slotPipelineRepository: SlotPipelineRepository,
    private val slotPipelineChangeRepository: SlotPipelineChangeRepository,
    private val slotPipelineAdmissionRuleStatusRepository: SlotPipelineAdmissionRuleStatusRepository,
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

    private fun <C, D> getEligibleBuilds(
        slot: Slot,
        rule: SlotAdmissionRule<C, D>,
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

    private fun <C, D> isBuildEligible(
        slot: Slot,
        rule: SlotAdmissionRule<C, D>,
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
        // Cancelling all current pipelines
        slotPipelineRepository.forAllActivePipelines(slot) { pipeline ->
            cancelPipeline(pipeline, "Cancelled by more recent pipeline.")
        }
        // Creating the new pipeline
        val pipeline = SlotPipeline(slot = slot, build = build)
        // Saving the pipeline
        slotPipelineRepository.savePipeline(pipeline)
        // OK
        return pipeline
    }

    override fun findPipelines(slot: Slot): PaginatedList<SlotPipeline> {
        // TODO Security check
        return slotPipelineRepository.findPipelines(slot)
    }

    override fun cancelPipeline(pipeline: SlotPipeline, reason: String) {
        // TODO Security check
        changePipeline(
            pipeline = pipeline,
            status = SlotPipelineStatus.CANCELLED,
            message = reason,
        )
    }

    private fun changePipeline(
        pipeline: SlotPipeline,
        status: SlotPipelineStatus,
        message: String,
        override: Boolean = false,
        overrideMessage: String? = null,
    ) {
        val user = securityService.currentSignature.user.name
        val timestamp = Time.now
        slotPipelineChangeRepository.save(
            SlotPipelineChange(
                pipeline = pipeline,
                user = user,
                timestamp = timestamp,
                status = status,
                message = message,
                override = override,
                overrideMessage = overrideMessage,
            )
        )
        var updatedPipeline = pipeline.withStatus(status)
        if (status.finished) {
            updatedPipeline = updatedPipeline.withEnd(timestamp)
        }
        slotPipelineRepository.savePipeline(updatedPipeline)
    }

    override fun findPipelineById(id: String): SlotPipeline? {
        // TODO Security check
        return slotPipelineRepository.findPipelineById(id)
    }

    override fun getPipelineChanges(pipeline: SlotPipeline): List<SlotPipelineChange> {
        // TODO Security check
        return slotPipelineChangeRepository.findByPipeline(pipeline)
    }

    override fun startDeployment(pipeline: SlotPipeline, dryRun: Boolean): SlotPipelineDeploymentStatus {
        // TODO Security check
        // Always checking the project
        if (pipeline.build.project != pipeline.slot.project) {
            throw SlotPipelineProjectException(pipeline)
        }
        // Gets all the admission rules
        val configs = slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(pipeline.slot)
        // All rules must assert that the build is OK for being deployed
        val status = SlotPipelineDeploymentStatus(
            checks = configs.map { config ->
                checkDeployment(pipeline, config)
            }
        )
        // Actual start
        if (!dryRun) {
            // TODO Cancels all other pipelines for the slot
            // Marks this pipeline as deploying
            changePipeline(
                pipeline = pipeline,
                status = SlotPipelineStatus.DEPLOYING,
                message = "Deployment started",
            )
        }
        // OK
        return status
    }

    override fun finishDeployment(pipeline: SlotPipeline, forcing: Boolean, message: String): String? {
        // TODO Security check
        // Only last pipeline can be deployed
        val lastPipeline = getCurrentPipeline(pipeline.slot)
        if (lastPipeline?.id != pipeline.id) {
            return "Only the last pipeline can be deployed."
        }
        // Checking if pipeline is deploying
        if (lastPipeline.status != SlotPipelineStatus.DEPLOYING && !forcing) {
            return "Pipeline can be deployed only if deployment has been started first."
        }
        // Marking the pipeline as deployed
        changePipeline(
            pipeline = pipeline,
            status = SlotPipelineStatus.DEPLOYED,
            message = message,
            override = forcing,
            overrideMessage = if (forcing) {
                "Deployment was marked manually."
            } else {
                null
            },
        )
        // OK
        return null
    }

    private fun checkDeployment(
        pipeline: SlotPipeline,
        config: SlotAdmissionRuleConfig,
    ): SlotPipelineDeploymentCheck {
        // Checking the rule
        val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
        val check = checkDeployment(pipeline, config, rule)
        // Getting the rule status for this pipeline
        if (!check.check.status) {
            val slotPipelineAdmissionRuleStatus =
                slotPipelineAdmissionRuleStatusRepository.findStatusesByPipelineAndAdmissionRuleConfig(pipeline, config)
                    .firstOrNull { it.override }
            if (slotPipelineAdmissionRuleStatus != null) {
                return check.withOverride(slotPipelineAdmissionRuleStatus)
            }
        }
        // OK
        return check
    }

    private fun <C, D> checkDeployment(
        pipeline: SlotPipeline,
        config: SlotAdmissionRuleConfig,
        rule: SlotAdmissionRule<C, D>,
    ): SlotPipelineDeploymentCheck {
        // Parsing the config
        val ruleConfig = rule.parseConfig(config.ruleConfig)
        // Gets any data associated with this config & pipeline
        val state = findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, config.id)
        val ruleData = state?.data?.let {
            SlotPipelineAdmissionRuleData(
                timestamp = state.timestamp,
                user = state.user,
                data = rule.parseData(it),
            )
        }
        // Checking the rule
        return SlotPipelineDeploymentCheck(
            check = rule.isBuildDeployable(pipeline, config, ruleConfig, ruleData),
            ruleId = rule.id,
            ruleConfig = config.ruleConfig,
            ruleData = ruleData?.asJson(),
        )
    }

    override fun getCurrentPipeline(slot: Slot): SlotPipeline? {
        // TODO Security check
        return findPipelines(slot).pageItems.firstOrNull()
    }

    override fun getPipelineAdmissionRuleStatuses(pipeline: SlotPipeline): List<SlotPipelineAdmissionRuleStatus> {
        // TODO Security check
        return slotPipelineAdmissionRuleStatusRepository.findStatusesByPipeline(pipeline)
    }

    override fun overrideAdmissionRule(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        message: String
    ) {
        // TODO Security check
        // Checking that we are targeting the same slot
        checkSameSlot(pipeline, admissionRuleConfig)
        // Overriding the rule
        slotPipelineAdmissionRuleStatusRepository.saveStatus(
            SlotPipelineAdmissionRuleStatus(
                pipeline = pipeline,
                admissionRuleConfig = admissionRuleConfig,
                timestamp = Time.now,
                user = securityService.currentSignature.user.name,
                data = null,
                override = true,
                overrideMessage = message,
            )
        )
    }

    private fun checkSameSlot(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig
    ) {
        val configs = getAdmissionRuleConfigs(pipeline.slot)
        if (configs.none { it.id == admissionRuleConfig.id }) {
            throw SlotAdmissionRuleConfigIdNotFoundInSlotException(pipeline, admissionRuleConfig)
        }
    }

    override fun setupAdmissionRule(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        data: JsonNode
    ) {
        // TODO Security check
        // Checking that we are targeting the same slot
        checkSameSlot(pipeline, admissionRuleConfig)
        // Overriding the rule
        slotPipelineAdmissionRuleStatusRepository.saveStatus(
            SlotPipelineAdmissionRuleStatus(
                pipeline = pipeline,
                admissionRuleConfig = admissionRuleConfig,
                timestamp = Time.now,
                user = securityService.currentSignature.user.name,
                data = data,
                override = false,
                overrideMessage = null,
            )
        )
    }
}