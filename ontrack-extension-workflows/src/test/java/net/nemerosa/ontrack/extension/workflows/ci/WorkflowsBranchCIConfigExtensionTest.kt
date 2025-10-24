package net.nemerosa.ontrack.extension.workflows.ci

import io.mockk.mockk
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowsBranchCIConfigExtensionTest {

    private lateinit var extension: WorkflowsBranchCIConfigExtension

    @BeforeEach
    fun before() {
        extension = WorkflowsBranchCIConfigExtension(
            workflowsExtensionFeature = mockk(),
            structureService = mockk(),
            eventSubscriptionService = mockk(),
            workflowNotificationChannel = mockk(),
            jsonTypeBuilder = mockk(),
        )
    }

    @Test
    fun `Parsing of the workflow configuration`() {
        val json = mapOf(
            "BRONZE" to listOf(
                mapOf(
                    "name" to "On Bronze for release",
                    "nodes" to listOf(
                        mapOf(
                            "id" to "start",
                            "executorId" to "mock",
                            "data" to mapOf(
                                "text" to "Start bronze for release"
                            )
                        )
                    )
                )
            ),
            "RELEASE" to listOf(
                mapOf(
                    "name" to "On Release",
                    "nodes" to listOf(
                        mapOf(
                            "id" to "start",
                            "executorId" to "mock",
                            "data" to mapOf(
                                "text" to "Start release"
                            )
                        )
                    )
                )
            ),
        ).asJson()

        val config = extension.parseData(json)

        assertEquals(
            WorkflowsBranchCIConfig(
                promotions = mapOf(
                    "BRONZE" to listOf(
                        Workflow(
                            name = "On Bronze for release",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "start",
                                    executorId = "mock",
                                    data = mapOf("text" to "Start bronze for release").asJson(),
                                )
                            )
                        )
                    ),
                    "RELEASE" to listOf(
                        Workflow(
                            name = "On Release",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "start",
                                    executorId = "mock",
                                    data = mapOf("text" to "Start release").asJson(),
                                )
                            )
                        )
                    ),
                )
            ),
            config,
        )
    }

    @Test
    fun `Merging of workflow configurations`() {

        val defaults = WorkflowsBranchCIConfig(
            promotions = mapOf(
                "BRONZE" to listOf(
                    Workflow(
                        name = "On Bronze",
                        nodes = listOf(
                            WorkflowNode(
                                id = "start",
                                executorId = "mock",
                                data = mapOf("text" to "Start bronze").asJson(),
                            )
                        )
                    )
                ),
            )
        )

        val custom = WorkflowsBranchCIConfig(
            promotions = mapOf(
                "BRONZE" to listOf(
                    Workflow(
                        name = "On Bronze for release",
                        nodes = listOf(
                            WorkflowNode(
                                id = "start",
                                executorId = "mock",
                                data = mapOf("text" to "Start bronze for release").asJson(),
                            )
                        )
                    )
                ),
                "RELEASE" to listOf(
                    Workflow(
                        name = "On Release",
                        nodes = listOf(
                            WorkflowNode(
                                id = "start",
                                executorId = "mock",
                                data = mapOf("text" to "Start release").asJson(),
                            )
                        )
                    )
                ),
            )
        )

        val config = extension.mergeData(defaults, custom)

        assertEquals(
            WorkflowsBranchCIConfig(
                promotions = mapOf(
                    "BRONZE" to listOf(
                        Workflow(
                            name = "On Bronze",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "start",
                                    executorId = "mock",
                                    data = mapOf("text" to "Start bronze").asJson(),
                                )
                            )
                        ),
                        Workflow(
                            name = "On Bronze for release",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "start",
                                    executorId = "mock",
                                    data = mapOf("text" to "Start bronze for release").asJson(),
                                )
                            )
                        ),
                    ),
                    "RELEASE" to listOf(
                        Workflow(
                            name = "On Release",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "start",
                                    executorId = "mock",
                                    data = mapOf("text" to "Start release").asJson(),
                                )
                            )
                        )
                    ),
                )
            ),
            config,
        )

    }

}