package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.Build

/**
 * This service is responsible for checking the eligibility of a build into a slot pipeline.
 *
 * @param C Type of the configuration for the rule.
 * @param D Type of the data for the rule.
 */
interface SlotAdmissionRule<C, D> {

    /**
     * Unique ID for this rule
     */
    val id: String

    /**
     * Display name for this rule
     */
    val name: String

    /**
     * Name for the rule being configured
     */
    fun getConfigName(config: C): String

    /**
     * Parsing a configuration
     */
    fun parseConfig(jsonRuleConfig: JsonNode): C

    /**
     * Checks if this build can be injected into the pipeline of a slot.
     */
    fun isBuildEligible(
        build: Build,
        slot: Slot,
        config: C,
    ): Boolean

    /**
     * Checks if this build can be deployable into the slot
     */
    fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: C,
        ruleData: SlotPipelineAdmissionRuleData<D>?,
    ): Boolean

    /**
     * Gets a list of eligible builds for a slot pipeline.
     */
    fun getEligibleBuilds(
        slot: Slot,
        config: C,
        size: Int = 10,
    ): List<Build>

    /**
     * Parsing the stored/client data into typed data for this rule
     */
    fun parseData(node: JsonNode): D

}