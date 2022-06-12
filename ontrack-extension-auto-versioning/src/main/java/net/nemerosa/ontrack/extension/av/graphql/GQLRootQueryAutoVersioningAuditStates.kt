package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAutoVersioningAuditStates : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("autoVersioningAuditStates")
            .description("List of possible states for auto version audit entries")
            .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(GraphQLString))))
            .dataFetcher { _ ->
                AutoVersioningAuditState.values().map { it.name }.toList()
            }
            .build()
}