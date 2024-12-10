package net.nemerosa.ontrack.extension.environments.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowTestFixtures
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowTrigger
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class EnvironmentsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Autowired
    private lateinit var environmentsCascContext: EnvironmentsCascContext

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Test
    fun `Defining environments keeps existing environments by default`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "demo",
                    description = "",
                    order = 50,
                    tags = emptyList(),
                    image = false,
                )
            )
            casc(
                """
                ontrack:
                    config:
                        environments:
                            environments:
                                - name: staging
                                  description: Repetition environment
                                  order: 100
                                  tags:
                                    - release
                                - name: production
                                  order: 200
                                  tags:
                                    - release
            """.trimIndent()
            )
            assertNotNull(environmentService.findByName("staging")) {
                assertEquals("Repetition environment", it.description)
                assertEquals(100, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            assertNotNull(environmentService.findByName("production")) {
                assertEquals("", it.description)
                assertEquals(200, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            // Demo environment has been kept
            assertNotNull(environmentService.findByName("demo"), "Demo environment kept")
        }
    }

    @Test
    fun `Defining environments in an authoritative way`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "demo",
                    description = "",
                    order = 50,
                    tags = emptyList(),
                    image = false,
                )
            )
            casc(
                """
                ontrack:
                    config:
                        environments:
                            keepEnvironments: false
                            environments:
                                - name: staging
                                  description: Repetition environment
                                  order: 100
                                  tags:
                                    - release
                                - name: production
                                  order: 200
                                  tags:
                                    - release
            """.trimIndent()
            )
            assertNotNull(environmentService.findByName("staging")) {
                assertEquals("Repetition environment", it.description)
                assertEquals(100, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            assertNotNull(environmentService.findByName("production")) {
                assertEquals("", it.description)
                assertEquals(200, it.order)
                assertEquals(listOf("release"), it.tags)
            }
            // Demo environment has been deleted
            assertNull(environmentService.findByName("demo"), "Demo environment deleted")
        }
    }

    @Test
    fun `Rendering environments`() {
        asAdmin {
            deleteAllEnvironments()
            environmentService.save(
                Environment(
                    name = "staging",
                    description = "Repetition environment",
                    order = 100,
                    tags = listOf("release"),
                    image = false,
                )
            )
            environmentService.save(
                Environment(
                    name = "production",
                    description = "",
                    order = 200,
                    tags = listOf("release"),
                    image = false,
                )
            )
            val json = environmentsCascContext.render()
            assertEquals(
                mapOf(
                    "keepEnvironments" to true,
                    "environments" to listOf(
                        mapOf(
                            "name" to "staging",
                            "description" to "Repetition environment",
                            "order" to 100,
                            "tags" to listOf("release"),
                        ),
                        mapOf(
                            "name" to "production",
                            "description" to "",
                            "order" to 200,
                            "tags" to listOf("release"),
                        ),
                    ),
                    "slots" to emptyList<String>(),
                ).asJson(),
                json
            )
        }
    }

    @Test
    fun `Defining slots and rules with existing projects`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            casc(
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: staging
                                      description: Repetition environment
                                      order: 100
                                      tags:
                                        - release
                                    - name: production
                                      order: 200
                                      tags:
                                        - release
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: staging
                                          admissionRules:
                                            - ruleId: promotion
                                              ruleConfig:
                                                promotion: SILVER
                                        - name: production
                                          admissionRules:
                                            - ruleId: promotion
                                              ruleConfig:
                                                promotion: GOLD
                                            - ruleId: branchPattern
                                              ruleConfig:
                                                includes:
                                                  - "release-.*"
                                    - project: ${project.name}
                                      qualifier: demo
                                      environments:
                                        - name: staging
                                          admissionRules:
                                            - ruleId: promotion
                                              ruleConfig:
                                                promotion: SILVER
                                        - name: production
                                          admissionRules:
                                            - ruleId: promotion
                                              ruleConfig:
                                                promotion: GOLD
                                            - name: releaseOnly
                                              ruleId: branchPattern
                                              ruleConfig:
                                                includes:
                                                  - "release-.*"
                """.trimIndent()
            )
            val staging = environmentService.findByName("staging") ?: fail("Staging not found")
            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertNotNull(slotService.findSlotByProjectAndEnvironment(staging, project, Slot.DEFAULT_QUALIFIER)) {
                val rules = slotService.getAdmissionRuleConfigs(it)
                assertEquals(
                    mapOf(
                        "promotion" to mapOf(
                            "ruleId" to "promotion",
                            "ruleConfig" to mapOf(
                                "promotion" to "SILVER"
                            ).asJson()
                        )
                    ),
                    rules.associate { rule ->
                        rule.name to mapOf(
                            "ruleId" to rule.ruleId,
                            "ruleConfig" to rule.ruleConfig,
                        )
                    }
                )
            }
            assertNotNull(slotService.findSlotByProjectAndEnvironment(staging, project, "demo")) {
                val rules = slotService.getAdmissionRuleConfigs(it)
                assertEquals(
                    mapOf(
                        "promotion" to mapOf(
                            "ruleId" to "promotion",
                            "ruleConfig" to mapOf(
                                "promotion" to "SILVER"
                            ).asJson()
                        )
                    ),
                    rules.associate { rule ->
                        rule.name to mapOf(
                            "ruleId" to rule.ruleId,
                            "ruleConfig" to rule.ruleConfig,
                        )
                    }
                )
            }
            assertNotNull(slotService.findSlotByProjectAndEnvironment(production, project, Slot.DEFAULT_QUALIFIER)) {
                val rules = slotService.getAdmissionRuleConfigs(it)
                assertEquals(
                    mapOf(
                        "promotion" to mapOf(
                            "ruleId" to "promotion",
                            "ruleConfig" to mapOf(
                                "promotion" to "GOLD"
                            ).asJson()
                        ),
                        "branchPattern" to mapOf(
                            "ruleId" to "branchPattern",
                            "ruleConfig" to mapOf(
                                "includes" to listOf("release-.*"),
                            ).asJson()
                        ),
                    ),
                    rules.associate { rule ->
                        rule.name to mapOf(
                            "ruleId" to rule.ruleId,
                            "ruleConfig" to rule.ruleConfig,
                        )
                    }
                )
            }
            assertNotNull(slotService.findSlotByProjectAndEnvironment(production, project, "demo")) {
                val rules = slotService.getAdmissionRuleConfigs(it)
                assertEquals(
                    mapOf(
                        "promotion" to mapOf(
                            "ruleId" to "promotion",
                            "ruleConfig" to mapOf(
                                "promotion" to "GOLD"
                            ).asJson()
                        ),
                        "releaseOnly" to mapOf(
                            "ruleId" to "branchPattern",
                            "ruleConfig" to mapOf(
                                "includes" to listOf("release-.*"),
                            ).asJson()
                        ),
                    ),
                    rules.associate { rule ->
                        rule.name to mapOf(
                            "ruleId" to rule.ruleId,
                            "ruleConfig" to rule.ruleConfig,
                        )
                    }
                )
            }
        }
    }

    @Test
    fun `Rendering of slot and admission rules`() {
        asAdmin {
            deleteAllEnvironments()
            slotTestSupport.withSlot(qualifier = "demo") { slot ->
                val rule = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(
                    slot = slot,
                    promotion = "GOLD"
                )
                slotService.addAdmissionRuleConfig(
                    config = rule
                )
                val workflow = SlotWorkflowTestFixtures.testWorkflow()
                slotWorkflowService.addSlotWorkflow(
                    SlotWorkflow(
                        slot = slot,
                        trigger = SlotWorkflowTrigger.DEPLOYING,
                        workflow = workflow,
                    )
                )
                val json = environmentsCascContext.render()
                assertEquals(
                    mapOf(
                        "keepEnvironments" to true,
                        "environments" to listOf(
                            mapOf(
                                "name" to slot.environment.name,
                                "description" to "",
                                "order" to slot.environment.order,
                                "tags" to slot.environment.tags,
                            ),
                        ),
                        "slots" to listOf(
                            mapOf(
                                "project" to slot.project.name,
                                "qualifier" to "demo",
                                "description" to "",
                                "environments" to listOf(
                                    mapOf(
                                        "name" to slot.environment.name,
                                        "admissionRules" to listOf(
                                            mapOf(
                                                "name" to rule.name,
                                                "description" to "",
                                                "ruleId" to rule.ruleId,
                                                "ruleConfig" to rule.ruleConfig,
                                            )
                                        ),
                                        "workflows" to listOf(
                                            mapOf(
                                                "trigger" to "DEPLOYING",
                                                "name" to workflow.name,
                                                "nodes" to workflow.nodes,
                                            )
                                        )
                                    )
                                ),
                            )
                        ),
                    ).asJson(),
                    json
                )
            }
        }
    }

    @Test
    fun `Deleting unconfigured admission rules`() {
        asAdmin {
            deleteAllEnvironments()
            slotTestSupport.withSlot(qualifier = "demo") { slot ->
                val rule = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(
                    slot = slot,
                    promotion = "GOLD"
                )
                slotService.addAdmissionRuleConfig(
                    config = rule
                )
                casc(
                    """
                        ontrack:
                            config:
                                environments:
                                    environments:
                                        - name: ${slot.environment.name}
                                          order: ${slot.environment.order}
                                    slots:
                                        - project: ${slot.project.name}
                                          qualifier: demo
                                          environments:
                                            - name: ${slot.environment.name}
                                              admissionRules: []
                    """.trimIndent()
                )
                // Checks that the admission rule is gone
                val newRules = slotService.getAdmissionRuleConfigs(slot)
                assertTrue(
                    newRules.isEmpty(),
                    "Admission rule is gone"
                )
            }
        }
    }

    @Test
    fun `Idempotency of admission rules`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: production
                                      order: 200
                                      tags:
                                        - release
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: production
                                          admissionRules:
                                            - ruleId: promotion
                                              ruleConfig:
                                                promotion: GOLD
                                            - ruleId: branchPattern
                                              ruleConfig:
                                                includes:
                                                  - "release-.*"
                """.trimIndent()
            casc(cascYaml)
            casc(cascYaml) // Twice
            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertNotNull(slotService.findSlotByProjectAndEnvironment(production, project, Slot.DEFAULT_QUALIFIER)) {
                val rules = slotService.getAdmissionRuleConfigs(it)
                assertEquals(
                    mapOf(
                        "promotion" to mapOf(
                            "ruleId" to "promotion",
                            "ruleConfig" to mapOf(
                                "promotion" to "GOLD"
                            ).asJson()
                        ),
                        "branchPattern" to mapOf(
                            "ruleId" to "branchPattern",
                            "ruleConfig" to mapOf(
                                "includes" to listOf("release-.*"),
                            ).asJson()
                        ),
                    ),
                    rules.associate { rule ->
                        rule.name to mapOf(
                            "ruleId" to rule.ruleId,
                            "ruleConfig" to rule.ruleConfig,
                        )
                    }
                )
            }
        }
    }

    @Test
    fun `Registration of workflows using Casc`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: production
                                      order: 200
                                      tags:
                                        - release
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: production
                                          workflows:
                                            - name: Creation
                                              trigger: CREATION
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
                                            - name: Deploying
                                              trigger: DEPLOYING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DEPLOYED
                                              nodes:
                                                - id: deployed
                                                  executorId: mock
                                                  data:
                                                      text: Deployed
                """.trimIndent()
            casc(cascYaml)
            casc(cascYaml) // Testing the updates

            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertNotNull(
                slotService.findSlotByProjectAndEnvironment(
                    production,
                    project,
                    Slot.DEFAULT_QUALIFIER
                )
            ) { slot ->
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.CREATION).firstOrNull()
                ) { sw ->
                    assertEquals("Creation", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYING).firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYED).firstOrNull()
                ) { sw ->
                    assertEquals("Deployed", sw.workflow.name)
                }
            }
        }
    }

    @Test
    fun `Deletion of workflows using Casc`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: production
                                      order: 200
                                      tags:
                                        - release
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: production
                                          workflows:
                                            - name: Creation
                                              trigger: CREATION
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
                                            - name: Deploying
                                              trigger: DEPLOYING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DEPLOYED
                                              nodes:
                                                - id: deployed
                                                  executorId: mock
                                                  data:
                                                      text: Deployed
                """.trimIndent()
            casc(cascYaml)

            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertNotNull(
                slotService.findSlotByProjectAndEnvironment(
                    production,
                    project,
                    Slot.DEFAULT_QUALIFIER
                )
            ) { slot ->
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.CREATION).firstOrNull()
                ) { sw ->
                    assertEquals("Creation", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYING).firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYED).firstOrNull()
                ) { sw ->
                    assertEquals("Deployed", sw.workflow.name)
                }
            }

            // Deletion
            casc(
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: production
                                      order: 200
                                      tags:
                                        - release
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: production
                                          workflows:
                                            - name: Deploying
                                              trigger: DEPLOYING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DEPLOYED
                                              nodes:
                                                - id: deployed
                                                  executorId: mock
                                                  data:
                                                      text: Deployed
                """.trimIndent()
            )

            assertNotNull(
                slotService.findSlotByProjectAndEnvironment(
                    environmentService.findByName("production")
                        ?: fail("Production not found"),
                    project,
                    Slot.DEFAULT_QUALIFIER
                )
            ) { slot ->
                assertNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.CREATION).firstOrNull(),
                    "Creation workflow is gone"
                )
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYING).firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotWorkflowTrigger.DEPLOYED).firstOrNull()
                ) { sw ->
                    assertEquals("Deployed", sw.workflow.name)
                }
            }
        }
    }

    private fun deleteAllEnvironments() {
        environmentService.findAll().forEach {
            environmentService.delete(it)
        }
    }

}