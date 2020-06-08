package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import org.springframework.stereotype.Component

@Component
class GQLRootQueryUser(
        private val user: GQLTypeUser
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("user")
                    .description("Gets the current user")
                    .type(user.typeRef)
                    .dataFetcher { GQLTypeUser.Data() }
                    .build()
}