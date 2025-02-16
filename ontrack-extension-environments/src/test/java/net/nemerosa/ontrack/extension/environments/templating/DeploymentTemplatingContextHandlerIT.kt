package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class DeploymentTemplatingContextHandlerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var deploymentTemplatingContextHandler: DeploymentTemplatingContextHandler

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Changelog since last deployment`() {
        asAdmin {

            val slot = slotTestSupport.slot()
            val project = slot.project
            val branch = project.branch("main")

            val firstBuild = branch.build("1")

            // Finishing the first deployment
            val firstDeployment = slotService.startPipeline(slot, firstBuild)
            slotTestSupport.runAndFinishDeployment(firstDeployment)

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

            // Creating a second deployment
            val secondDeployment = slotService.startPipeline(slot, secondBuild)

            // Rendering a changelog
            val text = deploymentTemplatingContextHandler.render(
                data = DeploymentTemplatingContextData(
                    slotPipelineId = secondDeployment.id,
                ),
                field = "changelog",
                config = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
            )

            // Expecting a changelog
            assertEquals(
                """
                    * ISS-22 Some fixes are needed
                """.trimIndent().trim(),
                text.trim()
            )
        }
    }

}