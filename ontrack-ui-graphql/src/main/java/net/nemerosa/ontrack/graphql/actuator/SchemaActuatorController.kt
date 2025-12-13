package net.nemerosa.ontrack.graphql.actuator

import graphql.schema.idl.SchemaPrinter
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.graphql.execution.GraphQlSource
import org.springframework.stereotype.Component


@Component
@Endpoint(id = "graphql")
class SchemaActuatorController(
    private val graphQlSource: GraphQlSource,
) {

    @ReadOperation(produces = ["application/graphql-schema", "text/plain"])
    fun schemaDsl(): String {
        val schema = graphQlSource.schema()
        val printer = SchemaPrinter(
            SchemaPrinter.Options.defaultOptions()
                .includeScalarTypes(true)
                .includeSchemaDefinition(true)
        )
        return printer.print(schema)
    }

}