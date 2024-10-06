package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extensions.environments.SlotTestFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SlotStorageIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotStorage: SlotStorage

    @Test
    fun `Saving and retrieving a slot for an environment`() {
        environmentTestSupport.withEnvironment { env ->
            project {
                val slot = SlotTestFixtures.testSlot(env, project)
                slotStorage.save(slot)
                val saved = slotStorage.getById(slot.id)
                assertEquals(slot, saved)
            }
        }
    }

    @Test
    fun `Getting the list of slots for an environment`() {
        environmentTestSupport.withEnvironment { env ->
            project {
                val slot = SlotTestFixtures.testSlot(env, project)
                slotStorage.save(slot)
                val slots = slotStorage.findByEnvironment(env)
                assertEquals(
                    listOf(slot),
                    slots,
                )
            }
        }
    }

    @Test
    fun `Deleting an environment deletes also the linked slots`() {
        TODO()
    }

}