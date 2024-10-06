package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot

interface SlotStorage {

    fun save(slot: Slot)

    fun getById(id: String): Slot

    fun findByEnvironment(env: Environment): List<Slot>

}