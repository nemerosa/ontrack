package net.nemerosa.ontrack.extension.workflows.registry

import com.fasterxml.jackson.databind.JsonNode
import io.mockk.mockk
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidationException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkflowRegistryImplTest {

    private lateinit var storageService: StorageService
    private lateinit var workflowRegistry: WorkflowRegistry

    @BeforeEach
    fun setUp() {
        storageService = mockk()
        workflowRegistry = WorkflowRegistryImpl(storageService)
    }

    @Test
    fun `Validation on saving a Yaml workflow`() {
        val yaml = """
            name: Sample
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Start
                  parents:
                    - id: end
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: start
        """.trimIndent()
        assertFailsWith<WorkflowValidationException>(
            message = "Validation of the workflow returned the following errors:\n" +
                    "* The workflow contains at least one cycle."
        ) {
            workflowRegistry.saveYamlWorkflow(yaml)
        }
    }

    @Test
    fun `Parsing error on validation`() {
        val json = mapOf(
            // Missing name
            "nodes" to listOf(
                mapOf(
                    "id" to "start",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Test"
                    ),
                    "parents" to emptyList<JsonNode>()
                )
            )
        ).asJson()
        val validation = workflowRegistry.validateJsonWorkflow(json)
        assertTrue(validation.error)
        assertEquals(validation.errors.firstOrNull(), "Cannot parse workflow")
    }

    @Test
    fun `Name is required`() {
        val json = mapOf(
            "name" to " ",
            "nodes" to listOf(
                mapOf(
                    "id" to "start",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Test"
                    ),
                    "parents" to emptyList<JsonNode>()
                )
            )
        ).asJson()
        val validation = workflowRegistry.validateJsonWorkflow(json)
        assertTrue(validation.error)
        assertEquals(validation.errors.firstOrNull(), "Workflow name is required.")
    }

    @Test
    fun `At least one node is required`() {
        val json = mapOf(
            "name" to "Some name",
            "nodes" to emptyList<JsonNode>()
        ).asJson()
        val validation = workflowRegistry.validateJsonWorkflow(json)
        assertTrue(validation.error)
        assertEquals(validation.errors.firstOrNull(), "At least one node is required.")
    }

    @Test
    fun `No cycles`() {
        val json = mapOf(
            "name" to "Cycles",
            "nodes" to listOf(
                mapOf(
                    "id" to "start",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Start"
                    ),
                    "parents" to listOf(
                        mapOf("id" to "end")
                    )
                ),
                mapOf(
                    "id" to "middle",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Middle"
                    ),
                    "parents" to listOf(
                        mapOf("id" to "start")
                    )
                ),
                mapOf(
                    "id" to "end",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "End"
                    ),
                    "parents" to listOf(
                        mapOf("id" to "middle")
                    )
                ),
            )
        ).asJson()
        val validation = workflowRegistry.validateJsonWorkflow(json)
        assertTrue(validation.error)
        assertEquals(validation.errors.firstOrNull(), "The workflow contains at least one cycle.")
    }

    @Test
    fun `No error`() {
        val json = mapOf(
            "name" to "No error",
            "nodes" to listOf(
                mapOf(
                    "id" to "start",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Start"
                    ),
                    "parents" to emptyList<JsonNode>()
                ),
                mapOf(
                    "id" to "middle",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "Middle"
                    ),
                    "parents" to listOf(
                        mapOf("id" to "start")
                    )
                ),
                mapOf(
                    "id" to "end",
                    "executorId" to "mock",
                    "data" to mapOf(
                        "text" to "End"
                    ),
                    "parents" to listOf(
                        mapOf("id" to "middle")
                    )
                ),
            )
        ).asJson()
        val validation = workflowRegistry.validateJsonWorkflow(json)
        assertFalse(validation.error)
        assertTrue(validation.errors.isEmpty())
    }

}