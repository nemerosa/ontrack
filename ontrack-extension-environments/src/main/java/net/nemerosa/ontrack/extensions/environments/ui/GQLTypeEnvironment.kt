package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.stringListField
import org.springframework.stereotype.Component

@Component
class GQLTypeEnvironment : GQLType {

    override fun getTypeName(): String = Environment::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Description of an environment")
            .stringField(Environment::id)
            .stringField(Environment::name)
            .intField(Environment::order)
            .stringField(Environment::description)
            .stringListField(Environment::tags)
            .build()
}