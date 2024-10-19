package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.rules.core.PromotionSlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.rules.core.PromotionSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.rules.core.ValidationSlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.rules.core.ValidationSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class SlotPipelineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Starting a new pipeline for a build`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                build {
                    val pipeline = slotService.startPipeline(slot, this)
                    assertNotNull(pipeline.start)
                    assertNull(pipeline.end)
                    assertEquals(SlotPipelineStatus.ONGOING, pipeline.status)
                    // Getting the pipelines for this slot
                    val pipelines = slotService.findPipelines(slot).pageItems
                    assertEquals(listOf(pipeline.id), pipelines.map { it.id })
                    assertEquals(listOf(this), pipelines.map { it.build })
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
            assertEquals(1, changes.size)
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
                slot = pipeline.slot,
                config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(pipeline.slot),
            )
            // Build not promoted yet, pipeline is not deployable
            assertEquals(
                false,
                slotService.startDeployment(pipeline, dryRun = true).status,
                "Build not promoted yet, pipeline is not deployable"
            )
            // Build promoted, pipeline is deployable
            pipeline.build.promote(pl)
            assertEquals(
                true,
                slotService.startDeployment(pipeline, dryRun = true).status,
                "Build promoted, pipeline is deployable"
            )
        }
    }

    @Test
    fun `Getting the reasons why a pipeline is not deployable`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // Adds a promotion rule to the slot
            val pl = pipeline.build.branch.promotionLevel(name = "GOLD")
            slotService.addAdmissionRuleConfig(
                slot = pipeline.slot,
                config = SlotAdmissionRuleConfig(
                    slot = pipeline.slot,
                    name = "Promotion to GOLD",
                    description = null,
                    ruleId = PromotionSlotAdmissionRule.ID,
                    ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl.name).asJson(),
                ),
            )
            // Adds a validation rule to the slot
            val vs = pipeline.build.branch.validationStamp(name = "ready")
            slotService.addAdmissionRuleConfig(
                slot = pipeline.slot,
                config = SlotAdmissionRuleConfig(
                    slot = pipeline.slot,
                    name = "Validation to ready",
                    description = null,
                    ruleId = ValidationSlotAdmissionRule.ID,
                    ruleConfig = ValidationSlotAdmissionRuleConfig(validation = vs.name).asJson(),
                ),
            )
            // Build is promoted, not validated
            pipeline.build.promote(pl)
            // Dry-run to start the build deployment
            var deploymentStatus = slotService.startDeployment(pipeline, dryRun = true)
            // Deployment not started
            assertFalse(deploymentStatus.status, "Deployment not possible")
            // Reasons
            assertEquals(
                listOf(
                    SlotPipelineDeploymentCheck(
                        check = DeployableCheck(
                            status = true,
                            reason = "Build promoted"
                        ),
                        ruleId = PromotionSlotAdmissionRule.ID,
                        ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl.name).asJson(),
                        ruleData = null,
                    ),
                    SlotPipelineDeploymentCheck(
                        check = DeployableCheck(
                            status = false,
                            reason = "Build not validated"
                        ),
                        ruleId = ValidationSlotAdmissionRule.ID,
                        ruleConfig = ValidationSlotAdmissionRuleConfig(validation = vs.name).asJson(),
                        ruleData = null,
                    ),
                ),
                deploymentStatus.checks
            )
            // Passing the validation
            pipeline.build.validate(vs)
            deploymentStatus = slotService.startDeployment(pipeline, dryRun = true)
            assertTrue(deploymentStatus.status, "Deployment possible")
            // Reasons
            assertEquals(
                listOf(
                    SlotPipelineDeploymentCheck(
                        check = DeployableCheck(
                            status = true,
                            reason = "Build promoted"
                        ),
                        ruleId = PromotionSlotAdmissionRule.ID,
                        ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl.name).asJson(),
                        ruleData = null,
                    ),
                    SlotPipelineDeploymentCheck(
                        check = DeployableCheck(
                            status = true,
                            reason = "Build validated"
                        ),
                        ruleId = ValidationSlotAdmissionRule.ID,
                        ruleConfig = ValidationSlotAdmissionRuleConfig(validation = vs.name).asJson(),
                        ruleData = null,
                    ),
                ),
                deploymentStatus.checks
            )
        }
    }

    @Test
    fun `Marking a pipeline as deploying`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            slotService.startDeployment(pipeline, dryRun = false)
            // Gets the pipeline
            val latestPipeline = slotService.getCurrentPipeline(pipeline.slot) ?: fail("Could not find pipeline")
            assertEquals(
                SlotPipelineStatus.DEPLOYING,
                latestPipeline.status,
            )
        }
    }

    @Test
    fun `Marking a pipeline as deployed (only if it was deploying)`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            assertTrue(slotService.startDeployment(pipeline, dryRun = false).status, "Deployment started")
            val finishStatus = slotService.finishDeployment(pipeline)
            assertEquals(true, finishStatus.deployed, "Deployment finished")
            // Gets the pipeline
            val latestPipeline = slotService.getCurrentPipeline(pipeline.slot) ?: fail("Could not find pipeline")
            assertEquals(
                SlotPipelineStatus.DEPLOYED,
                latestPipeline.status,
            )
            val change = slotService.getPipelineChanges(pipeline).firstOrNull()
            assertNotNull(change) {
                assertEquals(SlotPipelineStatus.DEPLOYED, it.status)
                assertEquals("Deployment finished", it.message)
                assertEquals(false, it.override)
                assertEquals(null, it.overrideMessage)
            }
        }
    }

    @Test
    fun `Overriding the pipeline deployment even if it was not deploying`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // By default, not possible to mark this pipeline as deployed
            var finishStatus = slotService.finishDeployment(pipeline)
            assertFalse(finishStatus.deployed, "Deployment not possible")
            assertEquals(
                "Pipeline can be deployed only if deployment has been started first.",
                finishStatus.message,
                "Deployment completion not possible"
            )
            // Forcing the deployment
            finishStatus = slotService.finishDeployment(pipeline, forcing = true, message = "Deployment forced")
            assertTrue(finishStatus.deployed, "Deployment done")
            // Checking the change
            val change = slotService.getPipelineChanges(pipeline).firstOrNull()
            assertNotNull(change) {
                assertEquals(SlotPipelineStatus.DEPLOYED, it.status)
                assertEquals("Deployment forced", it.message)
                assertEquals(true, it.override)
                assertEquals("Deployment was marked manually.", it.overrideMessage)
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
                name = "Promotion to GOLD",
                description = null,
                ruleId = PromotionSlotAdmissionRule.ID,
                ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = pl.name).asJson(),
            )
            slotService.addAdmissionRuleConfig(
                slot = pipeline.slot,
                config = admissionRuleConfig,
            )
            // By default, we cannot mark the build for deployment because rule is not complete
            var status = slotService.startDeployment(pipeline, dryRun = false)
            assertFalse(status.status, "Pipeline admission not possible")
            // Overriding the rule
            slotService.overrideAdmissionRule(
                pipeline = pipeline,
                admissionRuleConfig = admissionRuleConfig,
                message = "Because I want to",
            )
            // Deployment is now possible
            status = slotService.startDeployment(pipeline, dryRun = false)
            assertTrue(status.status, "Pipeline admission is now possible")
            // Checking that admission rule status
            val admissionRuleStatus = slotService.getPipelineAdmissionRuleStatuses(pipeline)
                .find { it.admissionRuleConfig.id == admissionRuleConfig.id }
            assertNotNull(
                admissionRuleStatus,
                "Admission rule status found"
            ) {
                assertNull(it.data, "No stored status for this rule")
                assertTrue(it.override, "Rule was overridden")
                assertEquals("Because I want to", it.overrideMessage)
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
                    assertEquals(SlotPipelineStatus.ONGOING, it.status)
                }
            }
        }
    }

}