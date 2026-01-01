package net.nemerosa.ontrack.extension.environments.ci

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnvironmentsCIConfigExtensionIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Test
    @AsAdminTest
    fun `Injection of environments based on the CI configuration`() {
        environmentService.findAll().forEach {
            environmentService.delete(it)
        }
        val project = configTestSupport.configureProject(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    project:
                      environments:
                        environments:
                          - name: self.yontrack.com
                            description: Production environment for Yontrack itself
                            order: 200
                            tags:
                              - yontrack
                              - release
                        slots:
                          - environments:
                              - name: self.yontrack.com
                                admissionRules:
                                  - ruleId: promotion
                                    ruleConfig:
                                      promotion: GOLD
                                  - ruleId: branchPattern
                                    ruleConfig:
                                      includes:
                                        - main
                                workflows:
                                  - name: Creation
                                    trigger: CANDIDATE
                                    nodes:
                                      - id: start
                                        executorId: mock
                                        data:
                                            text: Start
                                      - id: end
                                        parents:
                                          - id: start
                                        executorId: mock
                                        data:
                                            text: End
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic()
        )

        assertNotNull(environmentService.findByName("self.yontrack.com")) {
            assertEquals("Production environment for Yontrack itself", it.description)
            assertEquals(200, it.order)
            assertEquals(listOf("yontrack", "release"), it.tags)
        }

        val slot = slotService.findSlotsByProject(project).single()
        assertEquals("self.yontrack.com", slot.environment.name)

        assertEquals(2, slotService.getAdmissionRuleConfigs(slot).size)

        val workflow = slotWorkflowService.getSlotWorkflowsBySlot(slot).single()
        assertEquals("Creation", workflow.workflow.name)
        assertEquals(SlotPipelineStatus.CANDIDATE, workflow.trigger)

    }

}