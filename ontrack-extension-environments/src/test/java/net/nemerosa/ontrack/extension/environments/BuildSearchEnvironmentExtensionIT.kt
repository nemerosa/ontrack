package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.BuildSearchFormExtension
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BuildSearchEnvironmentExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Looking for the last deployed build in an environment`() {
        slotTestSupport.withSlot { slot ->
            // Deployment from another branch
            slotTestSupport.createRunAndFinishDeployment(
                branchName = "staging",
                slot = slot,
            )

            slot.project.apply {
                branch("main") {
                    val build = build()
                    val lastDeployment = slotService.startPipeline(slot, build)
                    slotTestSupport.runAndFinishDeployment(lastDeployment)

                    val nextBuild = build()
                    val nextDeployment = slotService.startPipeline(slot, nextBuild)
                    slotService.runDeployment(nextDeployment.id)

                    val builds = structureService.buildSearch(
                        projectId = slot.project.id,
                        form = BuildSearchForm(
                            extensions = listOf(
                                BuildSearchFormExtension(
                                    extension = "environment",
                                    value = slot.environment.name,
                                )
                            )
                        )
                    )

                    assertEquals(
                        listOf(build),
                        builds,
                        "Getting last build being deployed"
                    )
                }
            }
        }
    }

}