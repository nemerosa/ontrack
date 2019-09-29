package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import graphql.ExecutionResult
import net.nemerosa.ontrack.graphql.service.GraphQLService
import net.nemerosa.ontrack.json.ObjectMapperFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.util.concurrent.Callable

@Transactional
@RestController
@RequestMapping("/graphql")
class GraphqlController(
        private val graphQLService: GraphQLService
) {

    private val objectMapper = ObjectMapperFactory.create()

    /**
     * Request model
     */
    data class Request(
            val query: String,
            val variables: Map<String, Any>? = null,
            val operationName: String? = null
    )

    /**
     * GET end point
     */
    @GetMapping
    operator fun get(
            @RequestParam query: String,
            @RequestParam(required = false) variables: String?,
            @RequestParam(required = false) operationName: String?
    ): Callable<ResponseEntity<JsonNode>> {
        // Parses the arguments
        val arguments = decodeIntoMap(variables)
        // Runs the query
        return Callable {
            ResponseEntity.ok(
                    requestAsJson(
                            Request(
                                    query,
                                    arguments,
                                    operationName
                            )
                    )
            )
        }
    }

    /**
     * POST end point
     */
    @RequestMapping(method = [RequestMethod.POST])
    fun post(@RequestBody input: String): Callable<ResponseEntity<JsonNode>> {
        // Gets the components
        val request = objectMapper.readValue(input, Request::class.java)
        // Runs the query
        return Callable {
            ResponseEntity.ok(
                    requestAsJson(request)
            )
        }
    }

    /**
     * Request execution (JSON)
     */
    private fun requestAsJson(request: Request): JsonNode {
        return objectMapper.valueToTree(
                request(request)
        )

    }

    /**
     * Request execution
     */
    fun request(request: Request): ExecutionResult {
        return graphQLService.execute(
                request.query,
                request.variables ?: emptyMap(),
                request.operationName,
                true
        )
    }

    @Throws(IOException::class)
    private fun decodeIntoMap(variablesParam: String?): Map<String, Any> =
            (if (variablesParam.isNullOrBlank()) {
                emptyMap()
            } else {
                @Suppress("UNCHECKED_CAST")
                objectMapper.readValue(variablesParam, Map::class.java) as Map<String, Any>
            })

}
