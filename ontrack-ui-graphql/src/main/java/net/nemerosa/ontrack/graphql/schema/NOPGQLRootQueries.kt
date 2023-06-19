package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import org.springframework.stereotype.Component

@Component
class NOPGQLRootQueries : GQLRootQueries {
    override val fieldDefinitions: List<GraphQLFieldDefinition> = emptyList()
}