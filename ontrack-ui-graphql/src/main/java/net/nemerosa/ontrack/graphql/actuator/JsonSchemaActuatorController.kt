package net.nemerosa.ontrack.graphql.actuator

import com.fasterxml.jackson.databind.JsonNode
import graphql.introspection.IntrospectionQuery
import net.nemerosa.ontrack.json.asJson
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.graphql.execution.GraphQlSource
import org.springframework.stereotype.Component


@Component
@Endpoint(id = "graphql-json")
class JsonSchemaActuatorController(
    private val graphQlSource: GraphQlSource,
) {

    @ReadOperation
    fun schemaJson(): JsonNode {
        val graphQL = graphQlSource.graphQl()

        val result = graphQL.execute(IntrospectionQuery.INTROSPECTION_QUERY).toSpecification()
        return result.asJson()
    }

}