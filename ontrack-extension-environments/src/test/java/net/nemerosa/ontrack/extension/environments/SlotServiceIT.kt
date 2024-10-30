package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.rules.core.PromotionSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.service.SlotAdmissionRuleConfigNameFormatException
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.storage.SlotAlreadyDefinedException
import net.nemerosa.ontrack.extension.environments.storage.SlotIdAlreadyExistsException
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SlotServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Creating and retrieving a slot for an environment and a project`() {
        slotTestSupport.withSlot { slot ->
            val saved = slotService.getSlotById(slot.id)
            assertEquals(slot, saved)
        }
    }

    @Test
    fun `Adding a slot with a qualifier`() {
        slotTestSupport.withSlot(qualifier = "preview") { slot ->
            val saved = slotService.getSlotById(slot.id)
            assertEquals(slot, saved)
            assertEquals("preview", saved.qualifier)
        }
    }

    @Test
    fun `Cannot add a slot with same ID`() {
        slotTestSupport.withSlot { slot ->
            project {
                environmentTestSupport.withEnvironment { env ->
                    val other = SlotTestFixtures.testSlot(
                        id = slot.id,
                        env = env,
                        project = project,
                    )
                    assertFailsWith<SlotIdAlreadyExistsException> {
                        slotService.addSlot(other)
                    }
                }
            }
        }
    }

    @Test
    fun `Cannot add a slot with same project and qualifier`() {
        slotTestSupport.withSlot { slot ->
            val other = SlotTestFixtures.testSlot(
                id = UUID.randomUUID().toString(),
                env = slot.environment,
                project = slot.project,
                qualifier = slot.qualifier,
            )
            assertFailsWith<SlotAlreadyDefinedException> {
                slotService.addSlot(other)
            }
        }
    }

    @Test
    fun `Getting a list of slots for an environment`() {
        asAdmin {
            environmentTestSupport.withEnvironment { environment ->
                val slot1 = project<Slot> {
                    SlotTestFixtures.testSlot(env = environment, project = project).apply {
                        slotService.addSlot(this)
                    }
                }
                val slot2 = project<Slot> {
                    SlotTestFixtures.testSlot(env = environment, project = project).apply {
                        slotService.addSlot(this)
                    }
                }

                val slots = slotService.findSlotsByEnvironment(environment)
                assertEquals(
                    setOf(slot1, slot2),
                    slots.toSet()
                )
            }
        }
    }

    @Test
    fun `Finding a configured admission rule by ID`() {
        slotTestSupport.withSlot { slot ->
            val config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(slot)
            slotService.addAdmissionRuleConfig(
                config = config,
            )
            assertNotNull(
                slotService.findAdmissionRuleConfigById(config.id),
                "Found configured admission rule"
            ) {
                assertEquals(config, it)
            }
        }
    }

    @Test
    fun `Finds slots for a given project`() {
        slotTestSupport.withSlot { other ->
            project {
                slotTestSupport.withSlot(
                    project = project,
                ) { defaultQualifier ->
                    slotTestSupport.withSlot(
                        project = project,
                        qualifier = "demo",
                    ) { qualified ->
                        // Any qualifier
                        assertEquals(
                            setOf(defaultQualifier, qualified),
                            slotService.findSlotsByProject(project)
                        )
                        // Default qualifier
                        assertEquals(
                            setOf(defaultQualifier),
                            slotService.findSlotsByProject(project, qualifier = Slot.DEFAULT_QUALIFIER)
                        )
                        // Specific qualifier
                        assertEquals(
                            setOf(qualified),
                            slotService.findSlotsByProject(project, qualifier = "demo")
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the last deployed pipeline for a slot when there is no pipeline`() {
        slotTestSupport.withSlot { slot ->
            assertNull(
                slotService.getLastDeployedPipeline(slot),
                "No deployed pipeline"
            )
        }
    }

    @Test
    fun `Getting the last deployed pipeline for a slot when where a pipeline is ongoing`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            assertNull(
                slotService.getLastDeployedPipeline(pipeline.slot),
                "No deployed pipeline"
            )
        }
    }

    @Test
    fun `Getting the last deployed pipeline for a slot when a pipeline is deployed`() {
        slotTestSupport.withDeployedSlotPipeline { pipeline ->
            assertEquals(
                pipeline.id,
                slotService.getLastDeployedPipeline(pipeline.slot)?.id,
                "Getting the deployed pipeline"
            )
        }
    }

    @Test
    fun `Getting the last deployed pipeline for a slot when where several pipelines are deployed`() {
        slotTestSupport.withSlot { slot ->
            /* val pipeline1 = */ slotTestSupport.createStartAndDeployPipeline(slot = slot)
            val pipeline2 = slotTestSupport.createStartAndDeployPipeline(slot = slot)
            assertEquals(
                pipeline2.id,
                slotService.getLastDeployedPipeline(slot)?.id,
                "Getting the last deployed pipeline"
            )
        }
    }

    @Test
    fun `Getting the last deployed pipeline for a slot when where a pipeline is deployed before an ongoing one`() {
        slotTestSupport.withSlot { slot ->
            val pipeline1 = slotTestSupport.createStartAndDeployPipeline(slot = slot)
            /* val pipeline2 = */ slotTestSupport.createPipeline(slot = slot)
            assertEquals(
                pipeline1.id,
                slotService.getLastDeployedPipeline(slot)?.id,
                "Getting the last deployed pipeline"
            )
        }
    }

    @Test
    fun `Finding deployed slot pipelines for a build for a very simple case`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            // Starting the deployment
            slotService.startDeployment(pipeline, dryRun = false)
            // Finishing the deployment
            slotService.finishDeployment(pipeline)
            // Getting the pipelines for the build
            val pipelines = slotService.findLastDeployedSlotPipelinesByBuild(pipeline.build)
            assertEquals(
                listOf(
                    pipeline.id
                ),
                pipelines.map { it.id }
            )
        }
    }

    @Test
    fun `Finds deployed pipelines for a build when new deployment ongoing for other build`() {
        project {
            slotTestSupport.withSlot(project = project) { slot ->
                branch {
                    val build = build()
                    val deployedPipeline = slotService.startPipeline(slot, build).apply {
                        slotTestSupport.startAndDeployPipeline(this)
                    }

                    val other = build()
                    slotService.startPipeline(slot, other)

                    val pipelines = slotService.findLastDeployedSlotPipelinesByBuild(build)
                    assertEquals(
                        listOf(
                            deployedPipeline.id
                        ),
                        pipelines.map { it.id }
                    )
                }
            }
        }
    }

    @Test
    fun `Finds deployed pipelines for a build with several qualifiers`() {
        project {
            slotTestSupport.withSlot(project = project) { defaultSlot ->
                slotTestSupport.withSlot(project = project, qualifier = "demo") { demoSlot ->
                    branch {
                        val build = build()
                        val defaultPipeline = slotService.startPipeline(defaultSlot, build).apply {
                            slotTestSupport.startAndDeployPipeline(this)
                        }
                        val demoPipeline = slotService.startPipeline(demoSlot, build).apply {
                            slotTestSupport.startAndDeployPipeline(this)
                        }
                        val pipelines = slotService.findLastDeployedSlotPipelinesByBuild(build)
                        assertEquals(
                            setOf(
                                defaultPipeline.id,
                                demoPipeline.id,
                            ),
                            pipelines.map { it.id }.toSet()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting a list of eligible slots for a build`() {
        slotTestSupport.withSquareSlotsAndOther { project, stagingDefaultSlot, stagingDemoSlot, productionDefaultSlot, productionDemoSlot, _ ->
            // Creating a build on a non-release branch
            project.branch {
                build {
                    // Checking its eligible slots
                    val eligibleSlots = slotService.getEligibleSlotsForBuild(this)
                    val index = eligibleSlots.associate {
                        it.slot.id to it.eligible
                    }
                    assertEquals(
                        mapOf(
                            stagingDefaultSlot.id to true,
                            stagingDemoSlot.id to true,
                            productionDefaultSlot.id to false,
                            productionDemoSlot.id to true,
                        ),
                        index
                    )
                }
            }
        }
    }

    @Test
    fun `Slot admission rule names must be normalized`() {
        slotTestSupport.withSlot { slot ->
            val rule = SlotAdmissionRuleConfig(
                slot = slot,
                name = "Not a good name",
                description = "",
                ruleId = "promotion",
                ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = "GOLD").asJson(),
            )
            assertFailsWith<SlotAdmissionRuleConfigNameFormatException> {
                slotService.addAdmissionRuleConfig(rule)
            }
        }
    }
}
