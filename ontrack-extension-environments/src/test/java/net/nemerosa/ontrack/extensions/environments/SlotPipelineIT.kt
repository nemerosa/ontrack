package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

}