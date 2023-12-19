package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import org.springframework.stereotype.Component

/**
 * GraphQL query to get the settings
 */
@Component
class GQLRootQuerySettings(
    private val gqlTypeSettings: GQLTypeSettings,

) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("settings")
            .description("Ontrack settings")
            .dataFetcher { RootSettings() } // Must not return null, but content is not important
            .type(GraphQLNonNull(gqlTypeSettings.typeRef))
            // OK
            .build()

    internal class RootSettings
}

