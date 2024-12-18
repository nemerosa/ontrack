package net.nemerosa.ontrack.extension.environments.service.graph

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ProjectSlotGraphServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var projectSlotGraphService: ProjectSlotGraphService

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Test
    fun `Project environment graph using order only`() {
        asAdmin {
            val dev = slotTestSupport.slot(order = 10)
            val staging = slotTestSupport.slot(order = 20, project = dev.project)
            val acceptance = slotTestSupport.slot(order = 30, project = dev.project)
            val demo = slotTestSupport.slot(order = 30, project = dev.project)
            val production = slotTestSupport.slot(order = 40, project = dev.project)

            val slotGraph = projectSlotGraphService.slotGraph(dev.project)
            val nodes = slotGraph.slotNodes
            assertEquals(5, nodes.size)

            nodes[0].let {
                assertEquals(dev.id, it.slot.id)
                assertEquals(emptyList<Slot>(), it.parents)
            }

            nodes[1].let {
                assertEquals(staging.id, it.slot.id)
                assertEquals(listOf(dev.id), it.parents.map { p -> p.id })
            }

            nodes[2].let {
                assertEquals(acceptance.id, it.slot.id)
                assertEquals(listOf(staging.id), it.parents.map { p -> p.id })
            }

            nodes[3].let {
                assertEquals(demo.id, it.slot.id)
                assertEquals(listOf(staging.id), it.parents.map { p -> p.id })
            }

            nodes[4].let {
                assertEquals(production.id, it.slot.id)
                assertEquals(listOf(acceptance.id, demo.id), it.parents.map { p -> p.id })
            }
        }
    }

}