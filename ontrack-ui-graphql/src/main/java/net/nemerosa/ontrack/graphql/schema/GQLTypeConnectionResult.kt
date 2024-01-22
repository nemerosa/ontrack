package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.springframework.stereotype.Component

@Component
class GQLTypeConnectionResult : GQLType {

    override fun getTypeName(): String = ConnectionResult::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Result of testing a configuration")
            .enumField(ConnectionResult::type)
            .stringField(ConnectionResult::message)
            .build()
}
