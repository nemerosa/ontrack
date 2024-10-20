package net.nemerosa.ontrack.extensions.environments.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extensions.environments.*
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Build

interface SlotService {

    /**
     * Adding a new slot
     */
    fun addSlot(slot: Slot)

    /**
     * Getting a slot using its ID.
     */
    fun getSlotById(id: String): Slot

    /**
     * Gets the list of slots for an environment
     */
    fun findSlotsByEnvironment(environment: Environment): List<Slot>

    /**
     * Adds a configured slot admission rule to a slot
     */
    fun addAdmissionRuleConfig(slot: Slot, config: SlotAdmissionRuleConfig)

    /**
     * List of configured admission rules for a slot
     */
    fun getAdmissionRuleConfigs(slot: Slot): List<SlotAdmissionRuleConfig>

    /**
     * Deleting a configured admission rule in a slot
     */
    fun deleteAdmissionRuleConfig(config: SlotAdmissionRuleConfig)

    /**
     * Checks if a build is eligible for this slot.
     */
    fun isBuildEligible(slot: Slot, build: Build): Boolean

    /**
     * Gets the last N builds eligible for this slot
     */
    fun getEligibleBuilds(slot: Slot, count: Int = 10): List<Build>

    /**
     * Starting a pipeline
     */
    fun startPipeline(slot: Slot, build: Build): SlotPipeline

    /**
     *
     */
    fun findPipelines(slot: Slot): PaginatedList<SlotPipeline>

    /**
     * Cancelling a pipeline
     */
    fun cancelPipeline(pipeline: SlotPipeline, reason: String)

    /**
     * Getting a pipeline by ID
     */
    fun findPipelineById(id: String): SlotPipeline?

    /**
     * Getting the history of a pipeline
     */
    fun getPipelineChanges(pipeline: SlotPipeline): List<SlotPipelineChange>

    /**
     * Starts a deployment
     */
    fun startDeployment(pipeline: SlotPipeline, dryRun: Boolean): SlotPipelineDeploymentStatus

    /**
     * Gets the latest (current) pipeline for a slot
     */
    fun getCurrentPipeline(slot: Slot): SlotPipeline?

    /**
     * Marking a pipeline as being deployed
     */
    fun finishDeployment(
        pipeline: SlotPipeline,
        forcing: Boolean = false,
        message: String? = null,
    ): SlotPipelineDeploymentFinishStatus

    /**
     * Gets the stored states of admission rules for a given pipeline.
     */
    fun getPipelineAdmissionRuleStatuses(pipeline: SlotPipeline): List<SlotPipelineAdmissionRuleStatus>

    /**
     * Overriding an admission rule
     */
    fun overrideAdmissionRule(pipeline: SlotPipeline, admissionRuleConfig: SlotAdmissionRuleConfig, message: String)

    /**
     * Setting up some data for a rule in a pipeline
     */
    fun setupAdmissionRule(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        data: JsonNode,
    )

    /**
     * Finds a configured admission rule using its ID
     */
    fun findAdmissionRuleConfigById(id: String): SlotAdmissionRuleConfig?

}