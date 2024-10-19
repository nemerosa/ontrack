package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extensions.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extensions.environments.SlotTestSupport
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
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

}