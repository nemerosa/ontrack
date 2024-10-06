package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot

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

}