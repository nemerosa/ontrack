package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRule
import net.nemerosa.ontrack.model.structure.EntityStore
import org.springframework.stereotype.Repository

@Repository
class SlotStorageImpl(
    val entityStore: EntityStore,
) : SlotStorage {

    companion object {
        private const val STORE_PROJECT_SLOT = "Slot"
    }

    data class SlotRecord(
        val id: String,
        val environmentId: String,
        val description: String?,
        val admissionRules: List<SlotAdmissionRule>,
        val deployedId: Int?,
        val candidateId: Int?,
    )

    override fun save(slot: Slot) {
        entityStore.store(
            entity = slot.project,
            store = STORE_PROJECT_SLOT,
            name = slot.id,
            data = SlotRecord(
                id = slot.id,
                environmentId = slot.environment.id,
                description = slot.description,
                admissionRules = slot.admissionRules,
                deployedId = slot.deployed?.id(),
                candidateId = slot.candidate?.id(),
            )
        )
    }

    override fun getById(id: String): Slot {
        TODO("Not yet implemented")
    }

    override fun findByEnvironment(env: Environment): List<Slot> {
        TODO("Not yet implemented")
    }
}