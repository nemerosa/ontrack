package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SlotTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    fun withSlot(
        qualifier: String = Slot.DEFAULT_QUALIFIER,
        code: (slot: Slot) -> Unit,
    ) {
        project {
            environmentTestSupport.withEnvironment { env ->
                val slot = SlotTestFixtures.testSlot(
                    env = env,
                    project = project,
                    qualifier = qualifier,
                )
                slotService.addSlot(slot)
                code(slot)
            }
        }
    }

}