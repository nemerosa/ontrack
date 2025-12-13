package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.json.schema.JsonSchemaService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryJsonSchemaDefinitions(
    private val gqlTypeJsonSchemaDefinition: GQLTypeJsonSchemaDefinition,
    private val jsonSchemaService: JsonSchemaService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("jsonSchemaDefinitions")
            .description("Gets the JSON schema definitions")
            .type(listType(gqlTypeJsonSchemaDefinition.typeRef))
            .dataFetcher {
                jsonSchemaService.jsonSchemaDefinitions.sortedBy { it.key }
            }
            .build()
}