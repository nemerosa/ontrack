package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.security.EnvironmentList
import net.nemerosa.ontrack.extension.environments.security.SlotView
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Project
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
        environment: Environment? = null,
        project: Project = project(),
        qualifier: String = Slot.DEFAULT_QUALIFIER,
        code: (slot: Slot) -> Unit,
    ) {
        asAdmin {
            val env = environment ?: environmentTestSupport.withEnvironment {}
            val slot = SlotTestFixtures.testSlot(
                env = env,
                project = project,
                qualifier = qualifier,
            )
            slotService.addSlot(slot)
            code(slot)
        }
    }

    fun withSlotPipeline(
        branchName: String = uid("B"),
        qualifier : String = Slot.DEFAULT_QUALIFIER,
        code: (pipeline: SlotPipeline) -> Unit,
    ) {
        withSlot(qualifier = qualifier) { slot ->
            slot.project.branch(name = branchName) {
                build {
                    val pipeline = slotService.startPipeline(slot, this)
                    code(pipeline)
                }
            }
        }
    }

    fun createPipeline(
        branchName: String = uid("B"),
        slot: Slot
    ): SlotPipeline =
        slot.project.branch<SlotPipeline>(name = branchName) {
            val build = build()
            slotService.startPipeline(slot, build)
        }

    fun createStartAndDeployPipeline(
        branchName: String = uid("B"),
        slot: Slot
    ): SlotPipeline =
        createPipeline(branchName, slot).apply {
            startAndDeployPipeline(this)
        }

    fun startAndDeployPipeline(pipeline: SlotPipeline) {
        slotService.startDeployment(pipeline, dryRun = false)
        slotService.finishDeployment(pipeline)
    }

    fun withDeployedSlotPipeline(
        branchName: String = uid("B"),
        code: (pipeline: SlotPipeline) -> Unit,
    ) {
        withSlotPipeline(branchName = branchName) { pipeline ->
            startAndDeployPipeline(pipeline)
            code(pipeline)
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

    fun withSquareSlotsAndOther(
        code: (
            project: Project,
            stagingDefaultSlot: Slot,
            stagingDemoSlot: Slot,
            productionDefaultSlot: Slot,
            productionDemoSlot: Slot,
            other: Slot
        ) -> Unit
    ) {
        project {
            environmentTestSupport.withEnvironment { staging ->
                withSlot(
                    project = project,
                    environment = staging
                ) { stagingDefaultSlot ->
                    withSlot(
                        project = project,
                        environment = staging,
                        qualifier = "demo"
                    ) { stagingDemoSlot ->
                        environmentTestSupport.withEnvironment { production ->
                            withSlot(
                                project = project,
                                environment = production
                            ) { productionDefaultSlot ->
                                withSlot(
                                    project = project,
                                    environment = production,
                                    qualifier = "demo"
                                ) { productionDemoSlot ->

                                    // Only release branches for the production default slot
                                    slotService.addAdmissionRuleConfig(
                                        slot = productionDefaultSlot,
                                        config = SlotAdmissionRuleTestFixtures.testBranchPatternAdmissionRuleConfig(
                                            productionDefaultSlot
                                        )
                                    )

                                    // Whole project altogether
                                    withSlot { other ->

                                        // Running the code
                                        code(
                                            project,
                                            stagingDefaultSlot,
                                            stagingDemoSlot,
                                            productionDefaultSlot,
                                            productionDemoSlot,
                                            other,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}