package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.storage.SlotAlreadyDefinedException
import net.nemerosa.ontrack.extensions.environments.storage.SlotIdAlreadyExistsException
import net.nemerosa.ontrack.extensions.environments.storage.SlotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SlotServiceImpl(
    private val slotRepository: SlotRepository,
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

    override fun getSlotById(id: String): Slot {
        // TODO Security check
        return slotRepository.getSlotById(id)
    }
}