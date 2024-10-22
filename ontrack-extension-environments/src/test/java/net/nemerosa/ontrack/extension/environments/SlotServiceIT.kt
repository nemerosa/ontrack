package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.storage.SlotAlreadyDefinedException
import net.nemerosa.ontrack.extension.environments.storage.SlotIdAlreadyExistsException
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class SlotServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Creating and retrieving a slot for an environment and a project`() {
        slotTestSupport.withSlot { slot ->
            val saved = slotService.getSlotById(slot.id)
            assertEquals(slot, saved)
        }
    }

    @Test
    fun `Adding a slot with a qualifier`() {
        slotTestSupport.withSlot(qualifier = "preview") { slot ->
            val saved = slotService.getSlotById(slot.id)
            assertEquals(slot, saved)
            assertEquals("preview", saved.qualifier)
        }
    }

    @Test
    fun `Cannot add a slot with same ID`() {
        slotTestSupport.withSlot { slot ->
            project {
                environmentTestSupport.withEnvironment { env ->
                    val other = SlotTestFixtures.testSlot(
                        id = slot.id,
                        env = env,
                        project = project,
                    )
                    assertFailsWith<SlotIdAlreadyExistsException> {
                        slotService.addSlot(other)
                    }
                }
            }
        }
    }

    @Test
    fun `Cannot add a slot with same project and qualifier`() {
        slotTestSupport.withSlot { slot ->
            val other = SlotTestFixtures.testSlot(
                id = UUID.randomUUID().toString(),
                env = slot.environment,
                project = slot.project,
                qualifier = slot.qualifier,
            )
            assertFailsWith<SlotAlreadyDefinedException> {
                slotService.addSlot(other)
            }
        }
    }

    @Test
    fun `Getting a list of slots for an environment`() {
        asAdmin {
            environmentTestSupport.withEnvironment { environment ->
                val slot1 = project<Slot> {
                    SlotTestFixtures.testSlot(env = environment, project = project).apply {
                        slotService.addSlot(this)
                    }
                }
                val slot2 = project<Slot> {
                    SlotTestFixtures.testSlot(env = environment, project = project).apply {
                        slotService.addSlot(this)
                    }
                }

                val slots = slotService.findSlotsByEnvironment(environment)
                assertEquals(
                    setOf(slot1, slot2),
                    slots.toSet()
                )
            }
        }
    }

    @Test
    fun `Finding a configured admission rule by ID`() {
        slotTestSupport.withSlot { slot ->
            val config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(slot)
            slotService.addAdmissionRuleConfig(
                slot = slot,
                config = config,
            )
            assertNotNull(
                slotService.findAdmissionRuleConfigById(config.id),
                "Found configured admission rule"
            ) {
                assertEquals(config, it)
            }
        }
    }

}