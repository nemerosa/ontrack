package net.nemerosa.ontrack.extension.environments

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
     * Participates into the criteria to find eligible builds for a slot.
     */
    fun fillEligibilityCriteria(
        slot: Slot,
        config: C,
        queries: MutableList<String>,
        params: MutableMap<String, Any?>,
    )

    /**
     * Checks if this build can be deployable into the slot
     */
    fun isBuildDeployable(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        ruleConfig: C,
        ruleData: SlotPipelineAdmissionRuleData<D>?,
    ): DeployableCheck

    /**
     * Parsing the stored/client data into typed data for this rule
     */
    fun parseData(node: JsonNode): D

    /**
     * Checks if the given data is acceptable for the rule & its configuration.
     */
    fun checkData(ruleConfig: JsonNode, data: JsonNode) {}

    /**
     * Checks if the given configuration is valid for the rule
     */
    fun checkConfig(ruleConfig: JsonNode)

    /**
     * Checks if some data is complete for this rule
     */
    fun isDataComplete(ruleConfig: C, ruleData: D): Boolean = true

    /**
     * Given a configuration and some initial data, returns a list of fields
     * to enter some data for this rule.
     *
     * @return Empty if the rule is not editable
     */
    fun getInputFields(ruleConfig: C, data: D?): List<SlotAdmissionRuleInputField> = emptyList()

}