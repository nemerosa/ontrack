package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.rules.core.ManualApprovalSlotAdmissionRuleData
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRule
import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineAdmissionRuleChecksForAllRules
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

@QueueNoAsync
@AsAdminTest
class SlotPipelineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Test
    fun `Starting a new pipeline for a build`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                build {
                    val pipeline = slotService.startPipeline(slot, this)
                    assertNotNull(pipeline.start)
                    assertNull(pipeline.end)
                    assertEquals(SlotPipelineStatus.CANDIDATE, pipeline.status)
                    // Getting the pipelines for this slot
                    val pipelines = slotService.findPipelines(slot).pageItems
                    assertEquals(listOf(pipeline.id), pipelines.map { it.id })
                    assertEquals(listOf(this), pipelines.map { it.build })
                    // There must be at least one change
                    val changes = slotService.getPipelineChanges(pipeline)
                    assertEquals(1, changes.size)
                    val change = changes.first()
                    assertEquals(SlotPipelineStatus.CANDIDATE, change.status)
                }
            }
        }
    }

    @Test
    fun `Cancelling a pipeline with a message`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            slotService.cancelPipeline(pipeline, "Cancelling for test")
            // Gets the last version of this pipeline
            val lastPipeline = slotService.findPipelineById(pipeline.id) ?: fail("Could not find pipeline")
            assertEquals(SlotPipelineStatus.CANCELLED, lastPipeline.status)
            // Gets the changes for this pipeline
            val changes = slotService.getPipelineChanges(lastPipeline)
            assertEquals(2, changes.size)
            val change = changes.first()
            assertTrue(change.user.isNotBlank(), "Change user is filled in")
            assertEquals(SlotPipelineStatus.CANCELLED, change.status)
            assertEquals("Cancelling for test", change.message)
        }
    }

    @Test
    fun `Checking if a pipeline is deployable`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // Adds a promotion rule to the slot
            val pl = pipeline.build.branch.promotionLevel(name = "GOLD")
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(pipeline.slot),
            )
            // Build not promoted yet, pipeline is not deployable
            assertEquals(
                false,
                slotService.runDeployment(pipeline.id, dryRun = true).ok,
                "Build not promoted yet, pipeline is not deployable"
            )
            // Build promoted, pipeline is deployable
            pipeline.build.promote(pl)
            assertEquals(
                true,
                slotService.runDeployment(pipeline.id, dryRun = true).ok,
                "Build promoted, pipeline is deployable"
            )
        }
    }

    @Test
    fun `Getting the reasons why a pipeline is not deployable`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // Adds a promotion rule to the slot
            val pl1 = pipeline.build.branch.promotionLevel(name = "GOLD")
            val promotion1Config = SlotAdmissionRuleConfig(
                slot = pipeline.slot,
                name = "GoldPromotion",
                description = null,
                ruleId = PromotionSlotAdmissionRule.ID,
                ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl1.name).asJson(),
            )
            slotService.addAdmissionRuleConfig(
                config = promotion1Config,
            )
            // Adds another promotion rule to the slot
            val pl2 = pipeline.build.branch.promotionLevel(name = "DIAMOND")
            val promotion2Config = SlotAdmissionRuleConfig(
                slot = pipeline.slot,
                name = "DiamondPromotion",
                description = null,
                ruleId = PromotionSlotAdmissionRule.ID,
                ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl2.name).asJson(),
            )
            slotService.addAdmissionRuleConfig(
                config = promotion2Config,
            )
            // Build is promoted for 1, not for 2
            pipeline.build.promote(pl1)
            // Dry-run to start the build deployment
            var deploymentStatus = slotService.runDeployment(pipeline.id, dryRun = true)
            // Deployment not started
            assertFalse(deploymentStatus.ok, "Deployment not possible")
            // Reasons
            assertEquals(
                listOf(
                    SlotDeploymentCheck.nok("Build not promoted"),
                    SlotDeploymentCheck.ok("Build promoted"),
                ),
                slotService.getPipelineAdmissionRuleChecksForAllRules(pipeline)
            )
            // Passing the second promotion
            pipeline.build.promote(pl2)
            deploymentStatus = slotService.runDeployment(pipeline.id, dryRun = true)
            assertTrue(deploymentStatus.ok, "Deployment possible")
            // Reasons
            assertEquals(
                listOf(
                    SlotDeploymentCheck.ok("Build promoted"),
                    SlotDeploymentCheck.ok("Build promoted"),
                ),
                slotService.getPipelineAdmissionRuleChecksForAllRules(pipeline)
            )
        }
    }

    @Test
    fun `Marking a pipeline as running`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            slotService.runDeployment(pipeline.id, dryRun = false)
            // Gets the pipeline
            val latestPipeline = slotService.getCurrentPipeline(pipeline.slot) ?: fail("Could not find pipeline")
            assertEquals(
                SlotPipelineStatus.RUNNING,
                latestPipeline.status,
            )
        }
    }

    @Test
    fun `Marking a pipeline as deployed (only if it was deploying)`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            assertTrue(slotService.runDeployment(pipeline.id, dryRun = false).ok, "Deployment started")
            val finishStatus = slotService.finishDeployment(pipeline.id)
            assertEquals(true, finishStatus.ok, "Deployment finished")
            // Gets the pipeline
            val latestPipeline = slotService.getCurrentPipeline(pipeline.slot) ?: fail("Could not find pipeline")
            assertEquals(
                SlotPipelineStatus.DONE,
                latestPipeline.status,
            )
            val change = slotService.getPipelineChanges(pipeline).firstOrNull()
            assertNotNull(change) {
                assertEquals(SlotPipelineStatus.DONE, it.status)
                assertEquals(SlotPipelineChangeType.STATUS, it.type)
                assertEquals("Deployment finished", it.message)
                assertEquals(null, it.overrideMessage)
            }
        }
    }

    @Test
    fun `Overriding the pipeline deployment even if it was not deploying`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // By default, not possible to mark this pipeline as deployed
            var finishStatus = slotService.finishDeployment(pipeline.id)
            assertFalse(finishStatus.ok, "Deployment not possible")
            assertEquals(
                "Pipeline can be deployed only if deployment has been started first.",
                finishStatus.message,
                "Deployment completion not possible"
            )
            // Forcing the deployment
            finishStatus = slotService.finishDeployment(pipeline.id, forcing = true, message = "Deployment forced")
            assertTrue(finishStatus.ok, "Deployment done")
            // Checking the change
            val change = slotService.getPipelineChanges(pipeline).firstOrNull()
            assertNotNull(change) {
                assertEquals(SlotPipelineStatus.DONE, it.status)
                assertEquals("Deployment forced", it.message)
                assertEquals(SlotPipelineChangeType.STATUS, it.type)
                assertEquals("Deployment forced", it.overrideMessage)
            }
        }
    }

    @Test
    fun `Overriding a slot admission rule`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // Adds a promotion rule to the slot
            val pl = pipeline.build.branch.promotionLevel(name = "GOLD")
            val admissionRuleConfig = SlotAdmissionRuleConfig(
                slot = pipeline.slot,
                name = "GoldPromotion",
                description = null,
                ruleId = PromotionSlotAdmissionRule.ID,
                ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl.name).asJson(),
            )
            slotService.addAdmissionRuleConfig(
                config = admissionRuleConfig,
            )
            // By default, we cannot mark the build for deployment because rule is not complete
            var status = slotService.runDeployment(pipeline.id, dryRun = false)
            assertFalse(status.ok, "Pipeline admission not possible")
            // Overriding the rule
            slotService.overrideAdmissionRule(
                pipeline = pipeline,
                admissionRuleConfig = admissionRuleConfig,
                message = "Because I want to",
            )
            // Deployment is now possible
            status = slotService.runDeployment(pipeline.id, dryRun = false)
            assertTrue(status.ok, "Pipeline admission is now possible")
            // Checking that admission rule status
            val admissionRuleStatus = slotService.getPipelineAdmissionRuleStatuses(pipeline)
                .find { it.admissionRuleConfig.id == admissionRuleConfig.id }
            assertNotNull(
                admissionRuleStatus,
                "Admission rule status found"
            ) {
                assertNull(it.data, "No stored status for this rule")
                assertNotNull(it.override, "Rule was overridden") { override ->
                    assertEquals("Because I want to", override.message)
                }
            }
        }
    }

    @Test
    fun `Starting a pipeline cancels all other ongoing pipelines`() {
        slotTestSupport.withSlotPipeline { pipeline1 ->
            // On the same slot, creates another pipeline for another build
            pipeline1.build.branch.build {
                val pipeline2 = slotService.startPipeline(pipeline1.slot, this)
                // Checks that the pipeline 1 is cancelled
                assertNotNull(slotService.findPipelineById(pipeline1.id)) {
                    assertEquals(SlotPipelineStatus.CANCELLED, it.status)
                }
                // Checks that the pipeline 2 is active
                assertNotNull(slotService.findPipelineById(pipeline2.id)) {
                    assertEquals(SlotPipelineStatus.CANDIDATE, it.status)
                }
            }
        }
    }

    @Test
    fun `Getting a list of needed inputs for a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val config = SlotAdmissionRuleTestFixtures.testManualApprovalRuleConfig(pipeline.slot)
            slotService.addAdmissionRuleConfig(
                config
            )
            val inputs = slotService.getRequiredInputs(pipeline)
            assertEquals(
                listOf(
                    SlotAdmissionRuleInput(
                        config = config,
                        data = null,
                    )
                ),
                inputs,
            )
        }
    }

    @Test
    fun `Updating the data for a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val config = SlotAdmissionRuleTestFixtures.testManualApprovalRuleConfig(pipeline.slot)
            slotService.addAdmissionRuleConfig(config)

            assertFalse(slotService.getRequiredInputs(pipeline).isEmpty(), "Pipeline requires some input")

            slotService.setupAdmissionRule(
                pipeline = pipeline,
                admissionRuleConfig = config,
                data = mapOf(
                    "approval" to true,
                    "message" to "OK for me"
                ).asJson()
            )

            assertTrue(slotService.getRequiredInputs(pipeline).isEmpty(), "Pipeline doesn't require inputs any longer")

            val ruleStatus =
                slotService.getPipelineAdmissionRuleStatuses(pipeline)
                    .find { it.admissionRuleConfig.id == config.id }

            assertNotNull(ruleStatus, "Rule status data") {
                val data = it.data?.data?.parse<ManualApprovalSlotAdmissionRuleData>()
                assertEquals(
                    ManualApprovalSlotAdmissionRuleData(
                        approval = true,
                        message = "OK for me"
                    ),
                    data
                )
            }
        }
    }

    @Test
    fun `Deleting a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            slotService.deleteDeployment(pipeline.id)
            assertNull(
                slotService.findPipelineById(pipeline.id),
                "Deployment has been deleted"
            )
        }
    }

    @Test
    fun `Starting a pipeline in forced DONE status must not run the workflows`() {
        slotTestSupport.withSlot { slot ->
            // Registering workflows for each status
            slotWorkflowService.addSlotWorkflow(
                SlotWorkflow(
                    slot = slot,
                    trigger = SlotPipelineStatus.CANDIDATE,
                    workflow = WorkflowParser.parseYamlWorkflow(
                        """
                            name: On candidate
                            nodes:
                              - id: start
                                executorId: mock
                                data:
                                  text: Candidate
                        """.trimIndent()
                    )
                )
            )
            slotWorkflowService.addSlotWorkflow(
                SlotWorkflow(
                    slot = slot,
                    trigger = SlotPipelineStatus.RUNNING,
                    workflow = WorkflowParser.parseYamlWorkflow(
                        """
                            name: On running
                            nodes:
                              - id: start
                                executorId: mock
                                data:
                                  text: Running
                        """.trimIndent()
                    )
                )
            )
            slotWorkflowService.addSlotWorkflow(
                SlotWorkflow(
                    slot = slot,
                    trigger = SlotPipelineStatus.DONE,
                    workflow = WorkflowParser.parseYamlWorkflow(
                        """
                            name: On done
                            nodes:
                              - id: start
                                executorId: mock
                                data:
                                  text: Done
                        """.trimIndent()
                    )
                )
            )
            // Creating a pipeline in done mode
            slot.project.branch {
                build {
                    val pipeline = slotService.startPipeline(
                        slot = slot,
                        build = this,
                        forceDone = true,
                        forceDoneMessage = "Forcing a done pipeline",
                    )
                    assertEquals(
                        SlotPipelineStatus.DONE,
                        pipeline.status,
                        "Pipeline done"
                    )
                    // Checks that no workflow in candidate or running has run
                    val instances = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline)
                    assertEquals(
                        0,
                        instances.count { it.slotWorkflow.trigger == SlotPipelineStatus.CANDIDATE },
                        "No workflow on candidate"
                    )
                    assertEquals(
                        0,
                        instances.count { it.slotWorkflow.trigger == SlotPipelineStatus.RUNNING },
                        "No workflow on running"
                    )
                    assertEquals(
                        1,
                        instances.count { it.slotWorkflow.trigger == SlotPipelineStatus.DONE },
                        "1 workflow on done"
                    )
                    // Checks the force message
                    val change = slotService.getPipelineChanges(pipeline).firstOrNull()
                    assertNotNull(change) {
                        assertEquals(SlotPipelineStatus.DONE, it.status)
                        assertEquals("Forcing a done pipeline", it.message)
                        assertEquals(SlotPipelineChangeType.STATUS, it.type)
                        assertEquals("Forcing a done pipeline", it.overrideMessage)
                    }
                }
            }
        }
    }

}