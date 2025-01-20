package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotDeploymentActionStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnvironmentSlotAdmissionRuleIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Build eligible because project has a slot in previous environment`() {
        slotTestSupport.withSlot { previousSlot ->
            slotTestSupport.withSlot(project = previousSlot.project) { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )
                slot.project.branch {
                    val build = build()
                    assertTrue(
                        slotService.isBuildEligible(slot, build),
                        "Build eligible because previous environment has a slot for the same project"
                    )
                }
            }
        }
    }

    @Test
    fun `Build eligible because project has a qualified slot in previous environment`() {
        slotTestSupport.withSlot(qualifier = "demo") { previousSlot ->
            slotTestSupport.withSlot(project = previousSlot.project, qualifier = "demo") { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )
                slot.project.branch {
                    val build = build()
                    assertTrue(
                        slotService.isBuildEligible(slot, build),
                        "Build eligible because previous environment has a slot for the same project"
                    )
                }
            }
        }
    }

    @Test
    fun `Build not eligible because project has no slot in previous environment`() {
        slotTestSupport.withSlot { previousSlot ->
            // Different project
            slotTestSupport.withSlot { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )
                slot.project.branch {
                    val build = build()
                    assertFalse(
                        slotService.isBuildEligible(slot, build),
                        "Build NOT eligible because previous environment has NO slot for the same project"
                    )
                }
            }
        }
    }

    @Test
    fun `Build NOT eligible because project has not the same qualified slot in previous environment`() {
        slotTestSupport.withSlot(/* default qualifier */) { previousSlot ->
            slotTestSupport.withSlot(project = previousSlot.project, qualifier = "demo") { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot,
                        qualifier = "demo", // Not present in previous slot
                    )
                )
                slot.project.branch {
                    val build = build()
                    assertFalse(
                        slotService.isBuildEligible(slot, build),
                        "Build NOT eligible because previous environment has a slot for the same project but not for the same qualifier"
                    )
                }
            }
        }
    }

    @Test
    fun `Build deployed in previous environment default slot is deployable`() {
        withPreviousEnvironment(
            deployPreviousPipeline = true,
        ) { actionStatus ->
            assertTrue(
                actionStatus.ok,
                "Build is deployable because deployed in previous environment"
            )
        }
    }

    @Test
    fun `Build not deployed in previous environment default slot is not deployable`() {
        withPreviousEnvironment(
            deployPreviousPipeline = false,
        ) { actionStatus ->
            assertFalse(
                actionStatus.ok,
                "Build is NOT deployable because NOT deployed in previous environment"
            )
        }
    }

    @Test
    fun `Build not being the last deployed in previous environment default slot is not deployable`() {
        withPreviousEnvironment(
            deployPreviousPipeline = true,
            addPreviousPipeline = true,
        ) { actionStatus ->
            assertFalse(
                actionStatus.ok,
                "Build is NOT deployable because NOT deployed in previous environment"
            )
        }
    }

    @Test
    fun `Find eligible build because project has a slot in previous environment`() {
        slotTestSupport.withSlot { previousSlot ->
            slotTestSupport.withSlot(project = previousSlot.project) { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )
                slot.project.branch {
                    val build = build()
                    val eligibleBuilds = slotService.getEligibleBuilds(slot = slot).pageItems
                    assertEquals(
                        listOf(build.id),
                        eligibleBuilds.map { it.id },
                        "Build eligible because previous environment has a slot for the same project"
                    )
                }
            }
        }
    }

    @Test
    fun `Not finding eligible build because project has no slot in previous environment`() {
        slotTestSupport.withSlot { previousSlot ->
            // Different project
            slotTestSupport.withSlot { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )
                slot.project.branch {
                    /* val build = */ build()
                    val eligibleBuilds = slotService.getEligibleBuilds(slot = slot).pageItems
                    assertEquals(
                        emptyList(),
                        eligibleBuilds.map { it.id },
                        "Build NOT eligible because previous environment has NO slot for the same project"
                    )
                }
            }
        }
    }

    @Test
    fun `Find eligible builds and deployable builds`() {
        slotTestSupport.withSlot { previousSlot ->
            slotTestSupport.withSlot(project = previousSlot.project) { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousSlot
                    )
                )

                val branch = slot.project.branch()
                // Build not deployed in previous environment (but eligible all the same)
                val build1 = branch.build()
                // Build deployed in previous environment (deployable)
                val build2 = branch.build()
                val pipeline = slotService.startPipeline(previousSlot, build2)
                slotService.runDeployment(pipeline.id, dryRun = false)
                slotService.finishDeployment(pipeline.id)

                val eligibleBuilds = slotService.getEligibleBuilds(slot = slot).pageItems
                assertEquals(
                    listOf(build2.id, build1.id),
                    eligibleBuilds.map { it.id }
                )

                val deployableBuilds = slotService.getEligibleBuilds(slot = slot, deployable = true).pageItems
                assertEquals(
                    listOf(build2.id),
                    deployableBuilds.map { it.id }
                )
            }
        }
    }

    private fun withPreviousEnvironment(
        deployPreviousPipeline: Boolean,
        addPreviousPipeline: Boolean = false,
        qualifier: String = Slot.DEFAULT_QUALIFIER,
        code: (actionStatus: SlotDeploymentActionStatus) -> Unit,
    ) {
        slotTestSupport.withSlotPipeline(qualifier = qualifier) { previousPipeline ->
            if (deployPreviousPipeline) {
                slotTestSupport.runAndFinishDeployment(previousPipeline)
            }
            if (addPreviousPipeline) {
                // Creating another build
                val otherBuild = previousPipeline.build.branch.build()
                // Starting a more recent pipeline for this build in the previous slot
                slotService.startPipeline(
                    slot = previousPipeline.slot,
                    build = otherBuild,
                )
            }
            slotTestSupport.withSlot(project = previousPipeline.build.project, qualifier = qualifier) { slot ->
                slotService.addAdmissionRuleConfig(
                    config = SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                        slot,
                        previousPipeline.slot,
                        qualifier = qualifier,
                    )
                )
                val pipeline = slotService.startPipeline(slot, previousPipeline.build)
                val actionStatus = slotService.runDeployment(pipeline.id, dryRun = true)
                code(
                    actionStatus,
                )
            }
        }
    }

}