package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SlotGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Creating a list of slots`() {
        environmentTestSupport.withEnvironment { env1 ->
            environmentTestSupport.withEnvironment { env2 ->
                project {
                    run(
                        """
                        mutation CreateSlots(${'$'}environmentIds: [String!]!) {
                            createSlots(input: {
                                projectId: ${project.id},
                                environmentIds: ${'$'}environmentIds,
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """,
                        mapOf(
                            "environmentIds" to listOf(env1.id, env2.id)
                        )
                    ) { data ->
                        checkGraphQLUserErrors(data, "createSlots")
                        assertEquals(
                            listOf(env1 to project),
                            slotService.findSlotsByEnvironment(env1).map {
                                it.environment to it.project
                            }
                        )
                        assertEquals(
                            listOf(env2 to project),
                            slotService.findSlotsByEnvironment(env2).map {
                                it.environment to it.project
                            }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting slots for an environment`() {
        asAdmin {
            slotTestSupport.withSlot { slot ->
                run(
                    """
                {
                    environmentByName(name: "${slot.environment.name}") {
                        id
                        slots {
                            id
                        }
                    }
                }
            """
                ) { data ->
                    val environment = data.path("environmentByName")
                    val environmentId = environment.path("id").asText()
                    assertEquals(slot.environment.id, environmentId)
                    assertEquals(
                        listOf(slot.id),
                        environment.path("slots").map {
                            it.path("id").asText()
                        }
                    )
                }
            }
        }
    }

    @Test
    fun `Last eligible for a slot can be empty`() {
        asAdmin {
            slotTestSupport.withSlot { slot ->
                slot.project.branch {
                    run(
                        """
                        {
                            slotById(id: "${slot.id}") {
                                eligibleBuild {
                                    id
                                }
                            }
                        }
                    """.trimIndent()
                    ) { data ->
                        val slotData = data.path("slotById")
                        val build = slotData.path("eligibleBuild")
                        assertJsonNull(build, "No eligible build")
                    }
                }
            }
        }
    }

    @Test
    fun `Last eligible for a slot`() {
        asAdmin {
            slotTestSupport.withSlot { slot ->
                slot.project.branch {
                    val build = build()
                    run(
                        """
                        {
                            slotById(id: "${slot.id}") {
                                eligibleBuild {
                                    id
                                }
                            }
                        }
                    """.trimIndent()
                    ) { data ->
                        val slotData = data.path("slotById")
                        val buildData = slotData.path("eligibleBuild")
                        assertEquals(
                            build.id(),
                            buildData.path("id").asInt()
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
                    run(
                        """{
                            eligibleSlotsForBuild(buildId: $id) {
                                eligible
                                slot {
                                    id
                                }
                            }
                        }""".trimIndent()
                    ) { data ->
                        val eligibleSlots = data.path("eligibleSlotsForBuild")
                        val index = eligibleSlots.associate {
                            it.path("slot").path("id").asText() to it.path("eligible")
                                .asBoolean()
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
    }

    @Test
    fun `Getting a list of pipelines for a build`() {
        slotTestSupport.withSlotPipeline { _ ->
            slotTestSupport.withSlotPipeline { pipeline1 ->
                slotTestSupport.withSlot(project = pipeline1.slot.project) { slot2 ->
                    val pipeline2 = slotService.startPipeline(slot2, pipeline1.build)
                    run(
                        """{
                            build(id: ${pipeline1.build.id}) {
                                slotPipelines {
                                    id
                                }
                            }
                        }""".trimIndent()
                    ) { data ->
                        val pipelineIds = data.path("build")
                            .path("slotPipelines")
                            .map { it.path("id").asText() }
                        assertEquals(
                            listOf(
                                pipeline2.id,
                                pipeline1.id,
                            ),
                            pipelineIds
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting list of eligible builds for a slot`() {
        asAdmin {
            val project = project()
            val build = project.branch<Build> {
                build()
            }
            val slot1 = slotTestSupport.withSlot(project = project) {
                // Eligible
            }
            val slot2 = slotTestSupport.withSlot(project = project) {
                // Eligible
            }
            /* val slot3 = */ slotTestSupport.withSlot(project = project) {
            // Not eligible
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(it)
            )
        }

            run(
                """{
                    build(id: ${build.id}) {
                        eligibleSlots {
                            id
                        }
                    }
                }""".trimIndent()
            ) { data ->
                assertEquals(
                    setOf(
                        slot1.id,
                        slot2.id,
                    ),
                    data.path("build")
                        .path("eligibleSlots")
                        .map { it.path("id").asText() }
                        .toSet()
                )
            }
        }
    }

}