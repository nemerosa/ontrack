package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import graphql.GraphQL
import graphql.introspection.IntrospectionQuery
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.json.asJson
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/schema/graphql")
class GraphQLSchemaController(
        private val graphqlSchemaService: GraphqlSchemaService,
) {

    @GetMapping("json")
    fun schemaAsJson(): JsonNode {
        // Gets the schema
        val schema = graphqlSchemaService.schema
        // Runs the introspection query
        val graphQL = GraphQL.newGraphQL(schema).build()
        val executionResult = graphQL.execute(IntrospectionQuery.INTROSPECTION_QUERY)
        // OK
        return executionResult.toSpecification().asJson()
    }

}