package net.nemerosa.ontrack.extension.environments.notifications

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEventsFactory
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SlotPipelineCreationNotificationChannelIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var environmentsEventsFactory: EnvironmentsEventsFactory

    @Autowired
    private lateinit var notificationChannel: SlotPipelineCreationNotificationChannel

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Creating a pipeline into another slot for the same build`() {
        slotTestSupport.withSlot { staging ->
            environmentTestSupport.withEnvironment { productionEnv ->
                slotTestSupport.withSlot(
                    environment = productionEnv,
                    project = staging.project,
                ) { production ->
                    val config = SlotPipelineCreationNotificationChannelConfig(
                        environment = productionEnv.name,
                        qualifier = Slot.DEFAULT_QUALIFIER,
                    )
                    val stagingPipeline = slotTestSupport.createPipeline(slot = staging)
                    val event = environmentsEventsFactory.pipelineDeploying(stagingPipeline)
                    val result = notificationChannel.publish(
                        recordId = UUID.randomUUID().toString(),
                        config = config,
                        event = event,
                        context = emptyMap(),
                        template = null,
                        outputProgressCallback = { it }
                    )

                    val newPipelineId = result.output?.pipelineId
                    assertNotNull(newPipelineId) { id ->
                        assertNotNull(slotService.findPipelineById(id)) {
                            assertEquals(production, it.slot)
                        }
                    }
                }
            }
        }
    }
}