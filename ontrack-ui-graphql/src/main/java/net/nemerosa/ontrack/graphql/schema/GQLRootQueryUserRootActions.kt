package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Gets the list of all authorized actions for the current user
 */
@Component
class GQLRootQueryUserRootActions
@Autowired
constructor(
        private val contributors: List<GQLRootUserActionContributor>
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("userRootActions")
                    .description("List of actions authorized to the user")
                    .type(GraphQLObjectType.newObject()
                            .name("UserRootActions")
                            .fields(
                                    contributors
                                            .flatMap { it.userRootActions }
                                            .map { uriDef ->
                                                GraphQLFieldDefinition.newFieldDefinition()
                                                        .name(uriDef.name)
                                                        .type(Scalars.GraphQLString)
                                                        .dataFetcher({
                                                            if (uriDef.securityCheck()) {
                                                                uriDef.uri()
                                                            } else {
                                                                null
                                                            }
                                                        })
                                                        .build()
                                            }
                            )
                            .build()
                    )
                    .dataFetcher({ "" }) // Place holder object
                    .build()

}

