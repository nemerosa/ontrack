package net.nemerosa.ontrack.extension.environments.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowTestFixtures
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.test.TestUtils.resourceBytes
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.resourceBase64
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

/**
 * TODO Recurring issue where the project is created but not visible by the Casc, if we run in the same transaction...
 */
@Disabled("FLAKY")
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

    @Autowired
    private lateinit var testSCMExtension: TestSCMExtension

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

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
                            "image" to null,
                        ),
                        mapOf(
                            "name" to "production",
                            "description" to "",
                            "order" to 200,
                            "tags" to listOf("release"),
                            "image" to null,
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
                        trigger = SlotPipelineStatus.RUNNING,
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
                                "image" to null,
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
                                        "description" to "",
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
                                                "trigger" to "RUNNING",
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
                                            - name: Deploying
                                              trigger: RUNNING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DONE
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
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.CANDIDATE)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Creation", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.RUNNING)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.DONE)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Deployed", sw.workflow.name)
                }
            }
        }
    }

    @Test
    fun `Registration of workflows using Casc keeps existing workflows`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                keepEnvironments: true
                                environments:
                                    - name: production
                                      order: 200
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: production
                                          workflows:
                                            - name: Creation
                                              trigger: CANDIDATE
                                              nodes:
                                                - id: start
                                                  executorId: mock
                                                  data:
                                                      text: Start
                """.trimIndent()

            casc(cascYaml)

            // Getting the slot workflow

            val production = environmentService.findByName("production") ?: fail("Production not found")
            val slot = slotService.findSlotByProjectAndEnvironment(
                production,
                project,
                Slot.DEFAULT_QUALIFIER
            ) ?: fail("Could not find slot")
            val slotWorkflow = slotWorkflowService.getSlotWorkflowsBySlot(slot).single()

            // Re-applying the Casc

            casc(cascYaml)

            // Checking that the workflow has kept its ID

            assertNotNull(environmentService.findByName("production")) {
                assertNotNull(
                    slotService.findSlotByProjectAndEnvironment(
                        it,
                        project,
                        Slot.DEFAULT_QUALIFIER
                    )
                ) { slot ->
                    val newSlotWorkflow = slotWorkflowService.getSlotWorkflowsBySlot(slot).single()
                    // Checking this is the same
                    assertEquals(
                        slotWorkflow.id,
                        newSlotWorkflow.id,
                        "Slot workflow has not been overridden",
                    )
                }
            }
        }
    }

    @Test
    fun `Environments must not be declared twice`() {
        asAdmin {
            deleteAllEnvironments()
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                keepEnvironments: true
                                environments:
                                    - name: staging
                                      order: 100
                                    - name: production
                                      order: 200
                                    - name: production
                                      order: 300
                """.trimIndent()
            assertFailsWith<EnvironmentsCascException> {
                casc(cascYaml)
            }
        }
    }

    @Test
    fun `Environments must not be declared twice in a slot`() {
        asAdmin {
            deleteAllEnvironments()
            val project = project { }
            val cascYaml =
                """
                    ontrack:
                        config:
                            environments:
                                environments:
                                    - name: staging
                                      order: 100
                                    - name: production
                                      order: 200
                                slots:
                                    - project: ${project.name}
                                      environments:
                                        - name: staging
                                        - name: staging
                                        - name: production
                """.trimIndent()

            assertFailsWith<EnvironmentsCascException> {
                casc(cascYaml)
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
                                            - name: Deploying
                                              trigger: RUNNING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DONE
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
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.CANDIDATE)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Creation", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.RUNNING)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.DONE)
                        .firstOrNull()
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
                                              trigger: RUNNING
                                              nodes:
                                                - id: deploy
                                                  executorId: mock
                                                  data:
                                                      text: Deploy
                                            - name: Deployed
                                              trigger: DONE
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
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.CANDIDATE)
                        .firstOrNull(),
                    "Creation workflow is gone"
                )
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.RUNNING)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Deploying", sw.workflow.name)
                }
                assertNotNull(
                    slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(slot, SlotPipelineStatus.DONE)
                        .firstOrNull()
                ) { sw ->
                    assertEquals("Deployed", sw.workflow.name)
                }
            }
        }
    }

    @Test
    fun `Setting an image for an environment using the classpath`() {
        asAdmin {
            deleteAllEnvironments()
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
                                      image: classpath:/images/environment.png
                """.trimIndent()
            )
            val staging = environmentService.findByName("staging") ?: fail("Staging not found")
            assertFalse(staging.image, "No image for staging")

            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertTrue(production.image, "The production environment has an image")
        }
    }

    @Test
    fun `Setting an image for an environment using Base64`() {
        val image = resourceBase64("/images/environment.png")
        asAdmin {
            deleteAllEnvironments()
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
                                      image: |
                                        base64:$image
                """.trimIndent()
            )
            val staging = environmentService.findByName("staging") ?: fail("Staging not found")
            assertFalse(staging.image, "No image for staging")

            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertTrue(production.image, "The production environment has an image")
        }
    }

    @Test
    fun `Setting an image for an environment using an SCM`() {
        val config = uid("scm_")
        // Registering the image
        testSCMExtension.registerConfigForTestSCM(config) {
            withBinaryFile("images/environment.png", branch = null /* default branch */) {
                resourceBytes("/images/environment.png")
            }
        }
        asAdmin {
            deleteAllEnvironments()
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
                                      image: scm://test/$config/images/environment.png
                """.trimIndent()
            )
            val staging = environmentService.findByName("staging") ?: fail("Staging not found")
            assertFalse(staging.image, "No image for staging")

            val production = environmentService.findByName("production") ?: fail("Production not found")
            assertTrue(production.image, "The production environment has an image")
        }
    }

    @Test
    fun `Slot description is not set by default`() {
        asAdmin {
            deleteAllEnvironments()
            project {
                casc(
                    """
                        ontrack:
                            config:
                                environments:
                                  environments:
                                    - name: staging
                                  slots:
                                    - project: $name
                                      environments:
                                        - name: staging
                                
                    """.trimIndent()
                )
                assertNotNull(environmentService.findByName("staging")) { environment ->
                    assertNotNull(slotService.findSlotByProjectAndEnvironment(environment, this, "")) { slot ->
                        assertNull(slot.description, "Slot description not set")
                    }
                }
            }
        }
    }

    @Test
    fun `Slot description set by prefix`() {
        asAdmin {
            deleteAllEnvironments()
            project {
                casc(
                    """
                        ontrack:
                            config:
                                environments:
                                  environments:
                                    - name: staging
                                  slots:
                                    - project: $name
                                      description: Release
                                      environments:
                                        - name: staging
                                
                    """.trimIndent()
                )
                assertNotNull(environmentService.findByName("staging")) { environment ->
                    assertNotNull(slotService.findSlotByProjectAndEnvironment(environment, this, "")) { slot ->
                        assertEquals(
                            slot.description,
                            "Release $name"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Slot with qualifier description set by prefix`() {
        asAdmin {
            deleteAllEnvironments()
            project {
                casc(
                    """
                        ontrack:
                            config:
                                environments:
                                  environments:
                                    - name: staging
                                  slots:
                                    - project: $name
                                      qualifier: demo
                                      description: Release
                                      environments:
                                        - name: staging
                                
                    """.trimIndent()
                )
                assertNotNull(environmentService.findByName("staging")) { environment ->
                    assertNotNull(slotService.findSlotByProjectAndEnvironment(environment, this, "demo")) { slot ->
                        assertEquals(
                            slot.description,
                            "Release $name[demo]"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Slot description overridden by environment`() {
        asAdmin {
            deleteAllEnvironments()
            project {
                casc(
                    """
                        ontrack:
                            config:
                                environments:
                                  environments:
                                    - name: staging
                                  slots:
                                    - project: $name
                                      qualifier: demo
                                      description: Release
                                      environments:
                                        - name: staging
                                          description: Staging environment
                                
                    """.trimIndent()
                )
                assertNotNull(environmentService.findByName("staging")) { environment ->
                    assertNotNull(slotService.findSlotByProjectAndEnvironment(environment, this, "demo")) { slot ->
                        assertEquals(
                            slot.description,
                            "Staging environment"
                        )
                    }
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