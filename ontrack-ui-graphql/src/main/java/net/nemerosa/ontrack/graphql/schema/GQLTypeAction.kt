package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import org.springframework.stereotype.Component

/**
 * GraphQL type for [Action].
 */
@Component
class GQLTypeAction : GQLType {
    override fun getTypeRef() = GraphQLTypeReference("Action")

    override fun createType(): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("Action")
                    .field { it.name("id").type(GraphQLString) }
                    .field { it.name("name").type(GraphQLString) }
                    .field { it.name("type").type(GraphQLString) }
                    .field { it.name("uri").type(GraphQLString) }
                    .build()
}
