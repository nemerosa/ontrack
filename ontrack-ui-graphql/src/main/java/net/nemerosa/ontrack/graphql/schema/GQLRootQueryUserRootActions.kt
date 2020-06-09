package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Gets the list of all authorized actions for the current user
 */
@Component
class GQLRootQueryUserRootActions
@Autowired
constructor(
        private val userRootActions: GQLTypeUserRootActions
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("userRootActions")
                    .description("List of actions authorized to the user")
                    .deprecate("Use the `actions` field in the `User` type accessible through the `user` root query.")
                    .type(userRootActions.typeRef)
                    .dataFetcher { Unit } // Place holder object
                    .build()

}

