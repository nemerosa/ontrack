package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import graphql.ErrorClassification
import net.nemerosa.ontrack.graphql.schema.UserError
import net.nemerosa.ontrack.it.links.AbstractBranchLinksTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.ExecutionGraphQlResponse
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.ResponseError
import org.springframework.graphql.support.DefaultExecutionGraphQlRequest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.fail

@TestPropertySource(
    properties = [
        "spring.graphql.schema.locations=classpath*:graphql/**"
    ]
)
abstract class AbstractQLKTITSupport : AbstractBranchLinksTestSupport() {

    @Autowired
    private lateinit var executionGraphQlService: ExecutionGraphQlService

    private fun <T> authenticatedRun(
            query: String,
            variables: Map<String, Any?> = emptyMap(),
            responseProcessing: (response: ExecutionGraphQlResponse) -> T,
    ): T {
        // Task to run
        val code = { internalRun(query, variables, responseProcessing) }
        // Making sure we're at least authenticated
        return if (securityService.isLogged) {
            code()
        } else {
            asUser().call(code)
        }
    }

    fun run(
            query: String,
            variables: Map<String, Any?> = emptyMap()
    ): JsonNode = authenticatedRun(query, variables, ::assertNoErrors)

    fun run(
            query: String,
            variables: Map<String, Any?> = emptyMap(),
            code: (data: JsonNode) -> Unit = {},
    ) {
        code(authenticatedRun(query, variables, ::assertNoErrors))
    }

    fun runWithError(
            query: String,
            variables: Map<String, Any?> = emptyMap(),
            errorClassification: ErrorClassification? = null,
            errorMessage: String? = null,
    ) {
        authenticatedRun(query, variables) { response ->
            val errors = response.errors
            if (errors.isEmpty()) {
                fail("Expected some errors")
            } else if (errors.size > 1) {
                fail("Expected one error but got ${errors.size}.\n\n${
                    errors.joinToString("\n") {
                        "* [type = ${it.errorType}] ${it.message}"
                    }
                }")
            } else {
                val error: ResponseError = errors.first()
                val failMessageTitle = if (errorMessage == null) {
                    if (errorClassification == null) {
                        "At least one error is expected."
                    } else {
                        "At least one error with type = $errorClassification is expected."
                    }
                } else if (errorClassification == null) {
                    "At least one error with message = $errorMessage is expected."
                } else {
                    "At least one error with message = $errorMessage and type = $errorClassification is expected."
                }
                val failMessage = "$failMessageTitle\n\nbut error was:\n\n* type = ${error.errorType}\n* message = ${error.message}"
                if (errorClassification != null) {
                    assertEquals(errorClassification, error.errorType, failMessage)
                }
                if (errorMessage != null) {
                    assertEquals(errorMessage, error.message, failMessage)
                }
            }
        }
    }

    private fun assertNoErrors(response: ExecutionGraphQlResponse): JsonNode {
        if (response.errors.isNotEmpty()) {
            fail(
                    response.errors.joinToString("\n") { it.message ?: "Unknown error" }
            )
        }
        val data = response.getData<Any>().asJson()
        return assertIs<ObjectNode>(data)
    }

    private fun <T> internalRun(
            query: String,
            variables: Map<String, Any?> = emptyMap(),
            responseProcessing: (response: ExecutionGraphQlResponse) -> T,
    ): T {
        val result = executionGraphQlService.execute(
                DefaultExecutionGraphQlRequest(
                        /* document = */ query,
                        /* operationName = */ null,
                        /* variables = */ variables,
                        /* extensions = */ null,
                        /* id = */ uid("gql_"),
                        /* locale = */ null,
                )
        ).block()
        assertNotNull(result)
        return responseProcessing(result)
    }

    protected fun assertNoUserError(data: JsonNode, userNodeName: String): JsonNode {
        val userNode = data.path(userNodeName)
        val errors = userNode.path("errors")
        if (!errors.isNullOrNullNode() && errors.isArray && errors.size() > 0) {
            errors.forEach { error: JsonNode ->
                error.path("exception")
                        .takeIf { !it.isNullOrNullNode() }
                        ?.let { println("Error exception: ${it.asText()}") }
                error.path("location")
                        .takeIf { !it.isNullOrNullNode() }
                        ?.let { println("Error location: ${it.asText()}") }
                fail(error.path("message").asText())
            }
        }
        return userNode
    }

    protected fun assertUserError(
            data: JsonNode,
            userNodeName: String,
            message: String? = null,
            exception: String? = null
    ) {
        val errors = data.path(userNodeName).path("errors")
        if (errors.isNullOrNullNode()) {
            fail("Excepted the `errors` user node.")
        } else if (!errors.isArray) {
            fail("Excepted the `errors` user node to be an array.")
        } else if (errors.isEmpty) {
            fail("Excepted the `errors` user node to be a non-empty array.")
        } else {
            val error = errors.first()
            if (message != null) {
                assertEquals(message, error.path("message").asText())
            }
            if (exception != null) {
                assertEquals(exception, error.path("exception").asText())
            }
        }
    }

    protected fun checkGraphQLUserErrors(data: JsonNode, field: String): JsonNode {
        val payload = data.path(field)
        val node = payload.path("errors")
        if (node != null && node.isArray && node.size() > 0) {
            val error = node.first().parse<UserError>()
            throw IllegalStateException(error.toString())
        }
        return payload
    }

    protected fun checkGraphQLUserErrors(data: JsonNode, field: String, code: (payload: JsonNode) -> Unit) {
        val payload = checkGraphQLUserErrors(data, field)
        code(payload)
    }

}
