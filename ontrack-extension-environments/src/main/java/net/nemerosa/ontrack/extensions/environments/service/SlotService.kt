package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
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

}