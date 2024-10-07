package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.Build

/**
 * This service is responsible for checking the eligibility of a build into a slot pipeline.
 *
 * @param C Type of the configuration for the rule.
 */
interface SlotAdmissionRule<C> {

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
        build: Build,
        slot: Slot,
        config: C,
    ): Boolean

    /**
     * Gets a list of eligible builds for a slot pipeline.
     */
    fun getEligibleBuilds(
        slot: Slot,
        config: C,
        size: Int = 10,
    ): List<Build>

}