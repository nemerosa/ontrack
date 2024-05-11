package net.nemerosa.ontrack.extension.workflows.graphql

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredBooleanField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkflowsMutationsIT : AbstractWorkflowTestSupport() {

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

        asAdmin {
            run(
                """
                    mutation Validate(${'$'}workflow: JSON!) {
                        validateJsonWorkflow(input: {
                            workflow: ${'$'}workflow,
                        }) {
                            errors {
                                message
                            }
                            validation {
                                error
                                errors
                            }
                        }
                    }
                """,
                mapOf("workflow" to json)
            ) { data ->
                checkGraphQLUserErrors(data, "validateJsonWorkflow") { node ->
                    val validation = node.path("validation")
                    assertTrue(validation.getRequiredBooleanField("error"))
                    assertEquals(
                        "The workflow contains at least one cycle.",
                        validation.path("errors").path(0).asText()
                    )
                }
            }
        }
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

        asAdmin {
            run(
                """
                    mutation Validate(${'$'}workflow: JSON!) {
                        validateJsonWorkflow(input: {
                            workflow: ${'$'}workflow,
                        }) {
                            errors {
                                message
                            }
                            validation {
                                error
                                errors
                            }
                        }
                    }
                """,
                mapOf("workflow" to json)
            ) { data ->
                checkGraphQLUserErrors(data, "validateJsonWorkflow") { node ->
                    val validation = node.path("validation")
                    assertFalse(validation.getRequiredBooleanField("error"))
                    assertTrue(
                        validation.path("errors").isEmpty,
                        "No error message"
                    )
                }
            }
        }
    }

//    @Test
//    fun `No error`() {
//        val json = mapOf(
//            "name" to "No error",
//            "nodes" to listOf(
//                mapOf(
//                    "id" to "start",
//                    "executorId" to "mock",
//                    "data" to mapOf(
//                        "text" to "Start"
//                    ),
//                    "parents" to emptyList<JsonNode>()
//                ),
//                mapOf(
//                    "id" to "middle",
//                    "executorId" to "mock",
//                    "data" to mapOf(
//                        "text" to "Middle"
//                    ),
//                    "parents" to listOf(
//                        mapOf("id" to "start")
//                    )
//                ),
//                mapOf(
//                    "id" to "end",
//                    "executorId" to "mock",
//                    "data" to mapOf(
//                        "text" to "End"
//                    ),
//                    "parents" to listOf(
//                        mapOf("id" to "middle")
//                    )
//                ),
//            )
//        ).asJson()
//        val validation = workflowRegistry.validateJsonWorkflow(json)
//        assertFalse(validation.error)
//        assertTrue(validation.errors.isEmpty())
//    }

}