package net.nemerosa.ontrack.extension.environments.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.extension.environments.security.*
import net.nemerosa.ontrack.extension.environments.storage.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
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

    private inline fun <reified P : ProjectFunction> isSlotAccessible(slot: Slot): Boolean {
        return securityService.isGlobalFunctionGranted(EnvironmentList::class.java) &&
                securityService.isProjectFunctionGranted(slot.project, ProjectView::class.java) &&
                securityService.isProjectFunctionGranted(slot.project, SlotView::class.java) &&
                (P::class == SlotView::class || securityService.isProjectFunctionGranted(slot.project, P::class.java))
    }

    private inline fun <reified P : ProjectFunction> checkSlotAccess(slot: Slot) {
        securityService.checkGlobalFunction(EnvironmentList::class.java)
        securityService.checkProjectFunction(slot.project, ProjectView::class.java)
        securityService.checkProjectFunction(slot.project, SlotView::class.java)
        if (P::class != SlotView::class) {
            securityService.checkProjectFunction(slot.project, P::class.java)
        }
    }

    override fun addSlot(slot: Slot) {
        checkSlotAccess<SlotCreate>(slot)
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

    override fun findSlotsByEnvironment(environment: Environment): List<Slot> =
        if (securityService.isGlobalFunctionGranted(EnvironmentList::class.java)) {
            securityService.asAdmin {
                slotRepository.findByEnvironment(environment)
                    .filter {
                        securityService.isProjectFunctionGranted(it.project, ProjectView::class.java)
                    }
            }
                .sortedBy { it.project.name }
        } else {
            emptyList()
        }

    override fun addAdmissionRuleConfig(config: SlotAdmissionRuleConfig) {
        checkSlotAccess<SlotUpdate>(config.slot)
        // Controls the name
        config.checkName()
        // TODO Controls the provided configuration
        slotAdmissionRuleConfigRepository.addAdmissionRuleConfig(config)
    }

    override fun getRequiredInputs(pipeline: SlotPipeline): List<SlotAdmissionRuleInput> =
        if (pipeline.status == SlotPipelineStatus.ONGOING) {
            val rules = getAdmissionRuleConfigs(pipeline.slot)
            rules.mapNotNull { config ->
                val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
                getRequiredInput(pipeline, config, rule)
            }
        } else {
            emptyList()
        }

    override fun updatePipelineData(pipeline: SlotPipeline, inputs: List<SlotPipelineDataInput>) {
        checkSlotAccess<SlotPipelineData>(pipeline.slot)
        if (pipeline.status == SlotPipelineStatus.ONGOING) {
            val configs = getAdmissionRuleConfigs(pipeline.slot)
            val inputsPerRules = inputs.associate { it.name to it.values }
            configs.forEach { config ->
                val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
                updatePipelineRuleData(pipeline, rule, config, inputsPerRules[config.name])
            }
        } else {
            throw SlotPipelineDataNotOngoingException()
        }
    }

    private fun <C, D> updatePipelineRuleData(
        pipeline: SlotPipeline,
        rule: SlotAdmissionRule<C, D>,
        config: SlotAdmissionRuleConfig,
        slotPipelineInputValues: List<SlotPipelineDataInputValue>?,
    ) {
        if (slotPipelineInputValues != null) {
            val data = slotPipelineInputValues.associate {
                it.name to it.value
            }.asJson()
            rule.checkData(config.ruleConfig, data)
            setupAdmissionRule(
                pipeline = pipeline,
                admissionRuleConfig = config,
                data = data,
            )
        }
    }

    private fun <C, D> getRequiredInput(
        pipeline: SlotPipeline,
        config: SlotAdmissionRuleConfig,
        rule: SlotAdmissionRule<C, D>
    ): SlotAdmissionRuleInput? {
        val state = findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, config.id)
        val ruleConfig = rule.parseConfig(config.ruleConfig)
        val ruleData = state?.data?.let {
            SlotPipelineAdmissionRuleData(
                timestamp = state.timestamp,
                user = state.user,
                data = rule.parseData(it),
            )
        }
        return if (ruleData?.data != null && rule.isDataComplete(ruleConfig, ruleData.data)) {
            null
        } else {
            rule.getInputFields(ruleConfig, ruleData?.data)
                .takeIf { it.isNotEmpty() }
                ?.let { fields ->
                    SlotAdmissionRuleInput(
                        config = config,
                        fields = fields
                    )
                }
        }
    }

    override fun saveAdmissionRuleConfig(config: SlotAdmissionRuleConfig) {
        checkSlotAccess<SlotUpdate>(config.slot)
        // Controls the name
        config.checkName()
        // TODO Controls the provided configuration
        slotAdmissionRuleConfigRepository.saveAdmissionRuleConfig(config)
    }

    override fun findAdmissionRuleConfigById(id: String): SlotAdmissionRuleConfig? =
        slotAdmissionRuleConfigRepository.findAdmissionRuleConfigById(id)
            ?.takeIf { isSlotAccessible<ProjectView>(it.slot) }

    override fun getAdmissionRuleConfigs(slot: Slot): List<SlotAdmissionRuleConfig> {
        checkSlotAccess<SlotView>(slot)
        return slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(slot)
    }

    override fun deleteAdmissionRuleConfig(config: SlotAdmissionRuleConfig) {
        checkSlotAccess<SlotUpdate>(config.slot)
        slotAdmissionRuleConfigRepository.deleteAdmissionRuleConfig(config)
    }

    override fun isBuildEligible(slot: Slot, build: Build): Boolean {
        checkSlotAccess<SlotView>(slot)
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
        checkSlotAccess<SlotView>(slot)
        // Gets all the admission rules
        val configs = slotAdmissionRuleConfigRepository.getAdmissionRuleConfigs(slot)
        // Collecting parameters & queries
        val queries = mutableListOf<String>()
        val params = mutableMapOf<String, Any?>()
        configs.forEach { config ->
            fillEligibilityCriteria(slot, config, queries, params)
        }
        // Gets all eligible builds
        return slotRepository.getEligibleBuilds(slot, queries, params)
    }

    private fun fillEligibilityCriteria(
        slot: Slot,
        config: SlotAdmissionRuleConfig,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>,
    ) {
        val rule = slotAdmissionRuleRegistry.getRule(config.ruleId)
        fillEligibilityCriteria(slot, rule, config.ruleConfig, queries, params)
    }

    private fun <C, D> fillEligibilityCriteria(
        slot: Slot,
        rule: SlotAdmissionRule<C, D>,
        jsonRuleConfig: JsonNode,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>,
    ) {
        val ruleConfig = rule.parseConfig(jsonRuleConfig)
        rule.fillEligibilityCriteria(
            slot = slot,
            config = ruleConfig,
            queries = queries,
            params = params,
        )
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
        return slotRepository.getSlotById(id).apply {
            checkSlotAccess<SlotView>(this)
        }
    }

    override fun startPipeline(slot: Slot, build: Build): SlotPipeline {
        checkSlotAccess<SlotPipelineCreate>(slot)
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
        // Saving the initial change
        slotPipelineChangeRepository.save(
            SlotPipelineChange(
                pipeline = pipeline,
                user = securityService.currentSignature.user.name,
                timestamp = pipeline.start,
                status = pipeline.status,
                message = null,
                override = false,
                overrideMessage = null,
            )
        )
        // OK
        return findPipelineById(pipeline.id) ?: throw SlotPipelineIdNotFoundException(pipeline.id)
    }

    override fun findSlotsByProject(project: Project, qualifier: String?): Set<Slot> =
        slotRepository.findSlotsByProject(project, qualifier).filter {
            isSlotAccessible<ProjectView>(it)
        }.toSet()

    override fun getLastDeployedPipeline(slot: Slot): SlotPipeline? =
        slotPipelineRepository.findLastDeployedPipeline(slot)

    override fun getEligibleSlotsForBuild(build: Build): List<EligibleSlot> =
        slotRepository.findSlotsByProject(build.project, qualifier = null).map { slot ->
            EligibleSlot(
                slot = slot,
                eligible = isBuildEligible(slot, build),
            )
        }

    override fun findSlotByProjectAndEnvironment(environment: Environment, project: Project, qualifier: String): Slot? =
        slotRepository.findSlotByProjectAndEnvironment(environment, project, qualifier)

    override fun findLastDeployedSlotPipelinesByBuild(build: Build): Set<SlotPipeline> {
        // Finds the slots for the corresponding project
        val slots: Set<Slot> = findSlotsByProject(build.project)
        // For each slot, gets the last DEPLOYED pipeline and uses it
        // if for the given build
        val lastDeployedPipelines: List<SlotPipeline> = slots.mapNotNull { slot ->
            getLastDeployedPipeline(slot)
        }.filter {
            // Keeping only the pipelines for the given build
            it.build.id() == build.id()
        }
        // Grouping per qualifier
        val lastDeployedPipelinesPerQualifier = lastDeployedPipelines.groupBy { it.slot.qualifier }
        // For each group, gets the pipeline with the highest environment
        val highestPipelinePerQualifier = lastDeployedPipelinesPerQualifier.map { (qualifier, pipelines) ->
            qualifier to pipelines.maxBy { it.slot.environment.order }
        }.toMap()
        // Returning the pipelines
        return highestPipelinePerQualifier.values.toSet()
    }

    override fun findPipelines(slot: Slot): PaginatedList<SlotPipeline> {
        checkSlotAccess<SlotView>(slot)
        return slotPipelineRepository.findPipelines(slot)
    }

    override fun cancelPipeline(pipeline: SlotPipeline, reason: String) {
        checkSlotAccess<SlotPipelineCancel>(pipeline.slot)
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
        return slotPipelineRepository.findPipelineById(id)?.apply {
            checkSlotAccess<SlotView>(slot)
        }
    }

    override fun getPipelineChanges(pipeline: SlotPipeline): List<SlotPipelineChange> {
        checkSlotAccess<SlotView>(pipeline.slot)
        return slotPipelineChangeRepository.findByPipeline(pipeline)
    }

    override fun startDeployment(pipeline: SlotPipeline, dryRun: Boolean): SlotPipelineDeploymentStatus {
        checkSlotAccess<SlotPipelineStart>(pipeline.slot)
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

    override fun finishDeployment(
        pipeline: SlotPipeline,
        forcing: Boolean,
        message: String?
    ): SlotPipelineDeploymentFinishStatus {
        checkSlotAccess<SlotPipelineFinish>(pipeline.slot)
        // Only last pipeline can be deployed
        val lastPipeline = getCurrentPipeline(pipeline.slot)
        if (lastPipeline?.id != pipeline.id) {
            return SlotPipelineDeploymentFinishStatus.nok("Only the last pipeline can be deployed.")
        }
        // Checking if pipeline is deploying
        if (lastPipeline.status != SlotPipelineStatus.DEPLOYING && !forcing) {
            return SlotPipelineDeploymentFinishStatus.nok("Pipeline can be deployed only if deployment has been started first.")
        }
        // Actual message
        val actualMessage = message ?: "Deployment finished"
        // Marking the pipeline as deployed
        changePipeline(
            pipeline = pipeline,
            status = SlotPipelineStatus.DEPLOYED,
            message = actualMessage,
            override = forcing,
            overrideMessage = if (forcing) {
                "Deployment was marked manually."
            } else {
                null
            },
        )
        // OK
        return SlotPipelineDeploymentFinishStatus.ok(actualMessage)
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
            config = config,
            ruleData = ruleData?.asJson(),
        )
    }

    override fun getCurrentPipeline(slot: Slot): SlotPipeline? {
        checkSlotAccess<SlotView>(slot)
        return findPipelines(slot).pageItems.firstOrNull()
    }

    override fun getPipelineAdmissionRuleStatuses(pipeline: SlotPipeline): List<SlotPipelineAdmissionRuleStatus> {
        checkSlotAccess<SlotView>(pipeline.slot)
        return slotPipelineAdmissionRuleStatusRepository.findStatusesByPipeline(pipeline)
    }

    override fun overrideAdmissionRule(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        message: String
    ) {
        checkSlotAccess<SlotPipelineOverride>(pipeline.slot)
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
        // Checking that we are targeting the same slot
        checkSameSlot(pipeline, admissionRuleConfig)
        // Gets the rule
        val rule = slotAdmissionRuleRegistry.getRule(admissionRuleConfig.ruleId)
        // Checks the rule for the new state
        rule.checkData(admissionRuleConfig.ruleConfig, data)
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