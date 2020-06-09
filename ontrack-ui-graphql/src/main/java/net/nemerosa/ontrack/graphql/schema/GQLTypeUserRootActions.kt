package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.springframework.stereotype.Component

@Component
class GQLTypeUserRootActions(
        private val contributors: List<GQLRootUserActionContributor>
) : GQLType {

    companion object {
        const val USER_ROOT_ACTIONS_TYPE = "UserRootActions"
    }

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of actions authorized to the user")
            .fields(
                    contributors
                            .flatMap { it.userRootActions }
                            .map { uriDef ->
                                GraphQLFieldDefinition.newFieldDefinition()
                                        .name(uriDef.name)
                                        .type(Scalars.GraphQLString)
                                        .dataFetcher {
                                            if (uriDef.securityCheck()) {
                                                uriDef.uri()
                                            } else {
                                                null
                                            }
                                        }
                                        .build()
                            }
            )
            .build()


    override fun getTypeName(): String = USER_ROOT_ACTIONS_TYPE
}