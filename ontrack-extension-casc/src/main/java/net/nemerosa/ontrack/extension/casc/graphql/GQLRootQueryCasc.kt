package net.nemerosa.ontrack.extension.casc.graphql

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
class GQLRootQueryCasc(
    private val gqlTypeCasc: GQLTypeCasc
): GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("casc")
            .description("Configuration as Code")
            .type(GraphQLNonNull(gqlTypeCasc.typeRef))
            .dataFetcher { GQLTypeCasc.instance }
            .build()
}