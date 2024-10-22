package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.security.EnvironmentList
import net.nemerosa.ontrack.extension.environments.security.SlotView
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.test.TestUtils.uid
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

    fun withSlotPipeline(
        branchName: String = uid("B"),
        code: (pipeline: SlotPipeline) -> Unit,
    ) {
        withSlot { slot ->
            slot.project.branch(name = branchName) {
                build {
                    val pipeline = slotService.startPipeline(slot, this)
                    code(pipeline)
                }
            }
        }
    }

    fun withSlotUser(
        slot: Slot,
        name: String = uid("U"),
        code: (user: ConfigurableAccountCall) -> Unit,
    ) {
        val user = asUser(name = name)
            .with(EnvironmentList::class.java)
            .withProjectFunction(slot.project, SlotView::class.java)
            .withProjectFunction(slot.project, ProjectView::class.java)
        user.call {
            code(user)
        }
    }

}