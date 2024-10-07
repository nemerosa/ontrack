package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class SlotPipelineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Starting a new pipeline for a build`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                build {
                    val pipeline = slotService.startPipeline(slot, this)
                    assertNotNull(pipeline.start)
                    assertNull(pipeline.end)
                    assertEquals(SlotPipelineStatus.ONGOING, pipeline.status)
                    // Getting the pipelines for this slot
                    val pipelines = slotService.findPipelines(slot).pageItems
                    assertEquals(listOf(pipeline.id), pipelines.map { it.id })
                    assertEquals(listOf(this), pipelines.map { it.build })
                }
            }
        }
    }

    @Test
    fun `Cancelling a pipeline with a message`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            slotService.cancelPipeline(pipeline, "Cancelling for test")
            // Gets the last version of this pipeline
            val lastPipeline = slotService.findPipelineById(pipeline.id) ?: fail("Could not find pipeline")
            assertEquals(SlotPipelineStatus.CANCELLED, lastPipeline.status)
            // Gets the changes for this pipeline
            val changes = slotService.getPipelineChanges(lastPipeline)
            assertEquals(1, changes.size)
            val change = changes.first()
            assertTrue(change.user.isNotBlank(), "Change user is filled in")
            assertEquals(SlotPipelineStatus.CANCELLED, change.status)
            assertEquals("Cancelling for test", change.message)
        }
    }

}