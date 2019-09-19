package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import org.springframework.stereotype.Component

@Component
class NOPGQLFieldContributor : GQLFieldContributor {
    override fun getFields(type: Class<*>): List<GraphQLFieldDefinition> {
        return emptyList()
    }
}