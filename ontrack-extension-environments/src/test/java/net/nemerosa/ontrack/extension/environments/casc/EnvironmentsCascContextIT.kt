package net.nemerosa.ontrack.extension.environments.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowTestFixtures
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.it.NewTxRollbacked
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.test.TestUtils.resourceBytes
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.resourceBase64
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

@NewTxRollbacked
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

    @Test
    fun `CasC schema type`() {
        val type = environmentsCascContext.jsonType
        assertEquals(
            """
                {
                  "title": "EnvironmentsCascModel",
                  "description": null,
                  "properties": {
                    "environments": {
                      "items": {
                        "title": "EnvironmentCasc",
                        "description": "environments field",
                        "properties": {
                          "description": {
                            "description": "description field",
                            "type": "string"
                          },
                          "image": {
                            "description": "image field",
                            "type": "string"
                          },
                          "name": {
                            "description": "name field",
                            "type": "string"
                          },
                          "order": {
                            "description": "order field",
                            "type": "integer"
                          },
                          "tags": {
                            "items": {
                              "description": "tags field",
                              "type": "string"
                            },
                            "description": "tags field",
                            "type": "array"
                          }
                        },
                        "required": [
                          "name",
                          "order"
                        ],
                        "additionalProperties": false,
                        "type": "object"
                      },
                      "description": "environments field",
                      "type": "array"
                    },
                    "keepEnvironments": {
                      "description": "keepEnvironments field",
                      "type": "boolean"
                    },
                    "slots": {
                      "items": {
                        "title": "SlotCasc",
                        "description": "slots field",
                        "properties": {
                          "description": {
                            "description": "description field",
                            "type": "string"
                          },
                          "environments": {
                            "items": {
                              "title": "SlotEnvironmentCasc",
                              "description": "environments field",
                              "properties": {
                                "admissionRules": {
                                  "items": {
                                    "title": "SlotEnvironmentAdmissionRuleCasc",
                                    "description": "admissionRules field",
                                    "properties": {
                                      "actualName": {
                                        "description": "actualName field",
                                        "type": "string"
                                      },
                                      "description": {
                                        "description": "description field",
                                        "type": "string"
                                      },
                                      "name": {
                                        "description": "name field",
                                        "type": "string"
                                      },
                                      "ruleConfig": {
                                        "description": "ruleConfig field",
                                        "type": {}
                                      },
                                      "ruleId": {
                                        "description": "ruleId field",
                                        "type": "string"
                                      }
                                    },
                                    "required": [
                                      "actualName",
                                      "ruleConfig",
                                      "ruleId"
                                    ],
                                    "additionalProperties": false,
                                    "type": "object"
                                  },
                                  "description": "admissionRules field",
                                  "type": "array"
                                },
                                "name": {
                                  "description": "name field",
                                  "type": "string"
                                },
                                "workflows": {
                                  "items": {
                                    "title": "SlotWorkflowCasc",
                                    "description": "workflows field",
                                    "properties": {
                                      "name": {
                                        "description": "name field",
                                        "type": "string"
                                      },
                                      "nodes": {
                                        "items": {
                                          "title": "WorkflowNode",
                                          "description": "nodes field",
                                          "properties": {
                                            "description": {
                                              "description": "Description of the node in its workflow.",
                                              "type": "string"
                                            },
                                            "id": {
                                              "description": "Unique ID of the node in its workflow.",
                                              "type": "string"
                                            },
                                            "parents": {
                                              "items": {
                                                "title": "WorkflowParentNode",
                                                "description": "List of the IDs of the parents for this node",
                                                "properties": {
                                                  "id": {
                                                    "description": "ID of the parent node",
                                                    "type": "string"
                                                  }
                                                },
                                                "required": [
                                                  "id"
                                                ],
                                                "additionalProperties": false,
                                                "type": "object"
                                              },
                                              "description": "List of the IDs of the parents for this node",
                                              "type": "array"
                                            },
                                            "timeout": {
                                              "description": "Timeout in seconds (5 minutes by default)",
                                              "type": "integer"
                                            },
                                            "executorId": {
                                              "enum": [
                                                "mock",
                                                "notification",
                                                "pause",
                                                "slot-pipeline-creation",
                                                "slot-pipeline-deployed",
                                                "slot-pipeline-deploying"
                                              ],
                                              "description": "ID of the executor to use",
                                              "type": "string",
                                              "title": "Enum"
                                            }
                                          },
                                          "required": [
                                            "id",
                                            "data",
                                            "executorId"
                                          ],
                                          "additionalProperties": false,
                                          "oneOf": [
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "mock"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-mock"
                                                }
                                              }
                                            },
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "notification"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-notification"
                                                }
                                              }
                                            },
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "pause"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-pause"
                                                }
                                              }
                                            },
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "slot-pipeline-creation"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-slot-pipeline-creation"
                                                }
                                              }
                                            },
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "slot-pipeline-deployed"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-slot-pipeline-deployed"
                                                }
                                              }
                                            },
                                            {
                                              "properties": {
                                                "executorId": {
                                                  "const": "slot-pipeline-deploying"
                                                },
                                                "data": {
                                                  "${'$'}ref": "#/${'$'}defs/workflow-node-executor-slot-pipeline-deploying"
                                                }
                                              }
                                            }
                                          ],
                                          "type": "object"
                                        },
                                        "description": "nodes field",
                                        "type": "array"
                                      },
                                      "trigger": {
                                        "enum": [
                                          "CANDIDATE",
                                          "RUNNING",
                                          "CANCELLED",
                                          "DONE"
                                        ],
                                        "description": "trigger field",
                                        "type": "string",
                                        "title": "Enum"
                                      }
                                    },
                                    "required": [
                                      "name",
                                      "nodes",
                                      "trigger"
                                    ],
                                    "additionalProperties": false,
                                    "type": "object"
                                  },
                                  "description": "workflows field",
                                  "type": "array"
                                }
                              },
                              "required": [
                                "name"
                              ],
                              "additionalProperties": false,
                              "type": "object"
                            },
                            "description": "environments field",
                            "type": "array"
                          },
                          "project": {
                            "description": "project field",
                            "type": "string"
                          },
                          "qualifier": {
                            "description": "qualifier field",
                            "type": "string"
                          }
                        },
                        "required": [
                          "environments",
                          "project"
                        ],
                        "additionalProperties": false,
                        "type": "object"
                      },
                      "description": "slots field",
                      "type": "array"
                    }
                  },
                  "required": [
                    "environments"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

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

    private fun deleteAllEnvironments() {
        environmentService.findAll().forEach {
            environmentService.delete(it)
        }
    }

}