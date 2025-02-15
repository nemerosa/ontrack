package net.nemerosa.ontrack.extension.environments.changelog

import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SlotPipelineChangelogIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotWorkflowTestSupport: SlotWorkflowTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Autowired
    private lateinit var mockNotificationChannel: MockNotificationChannel

    @Test
    fun `Sending a changelog since last deployment`() {
        val target = uid("t-")
        asAdmin {
            startNewTransaction {
                val slot = slotTestSupport.slot()
                val project = slot.project
                val branch = project.branch("main")

                val firstBuild = branch.build("1")

                // Finishing the first deployment
                val firstDeployment = slotService.startPipeline(slot, firstBuild)
                slotTestSupport.runAndFinishDeployment(firstDeployment)

                // Registering a workflow on DONE sending the changelog since the last deployment
                slotWorkflowService.addSlotWorkflow(
                    SlotWorkflow(
                        slot = slot,
                        trigger = SlotPipelineStatus.DONE,
                        workflow = WorkflowParser.parseYamlWorkflow(
                            """
                                name: Changelog notification
                                nodes:
                                  - id: changelog
                                    executorId: notification
                                    data:
                                      channel: mock
                                      channelConfig:
                                        target: $target
                                      template: |
                                        ${'$'}{deployment.changelog}
                                      
                            """.trimIndent()
                        )
                    )
                )

                // Creating a new build
                val secondBuild = branch.build("2")

                // Configuring the project/branch with an SCM
                mockSCMTester.withMockSCMRepository {
                    branch.configureMockSCMBranch()

                    // Configuring the SCM for a changelog between two builds
                    firstBuild.apply {
                        repositoryIssue("ISS-20", "Last issue before the change log", type = "defect")
                        withRepositoryCommit("ISS-20 Last commit before the change log")
                    }
                    secondBuild.apply {
                        repositoryIssue("ISS-22", "Some fixes are needed", type = "defect")
                        withRepositoryCommit("ISS-22 Fixing some bugs")
                    }
                }

                // Creating & finishing a second deployment
                val secondDeployment = slotService.startPipeline(slot, secondBuild)
                slotTestSupport.runAndFinishDeployment(secondDeployment)

                // Returning the second pipeline
                secondDeployment
            } then { secondDeployment ->

                // Waits for the completion of workflows
                slotWorkflowTestSupport.waitForSlotWorkflowsToFinish(
                    pipeline = secondDeployment,
                    trigger = SlotPipelineStatus.DONE,
                )

                // Expecting a changelog
                val message = mockNotificationChannel.targetMessages(target).single()
                assertEquals(
                    """
                        * ISS-22 Some fixes are needed
                    """.trimIndent().trim(),
                    message.trim()
                )
            }
        }
    }

}