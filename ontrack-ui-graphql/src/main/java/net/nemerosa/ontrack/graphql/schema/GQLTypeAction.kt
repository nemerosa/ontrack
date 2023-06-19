package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * GraphQL type for [net.nemerosa.ontrack.model.support.Action].
 */
@Component
class GQLTypeAction : GQLType {

    override fun getTypeName() = "Action"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .field { it.name("id").type(GraphQLString) }
            .field { it.name("name").type(GraphQLString) }
            .field { it.name("type").type(GraphQLString) }
            .field { it.name("uri").type(GraphQLString) }
            .booleanField(Action::enabled)
            .build()
}
