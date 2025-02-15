package net.nemerosa.ontrack.graphql.schema.trigger

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.trigger.Trigger
import org.springframework.stereotype.Component

@Component
class GQLTypeTrigger : GQLType {

    override fun getTypeName(): String = Trigger::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Trigger for something")
            .stringField(Trigger<*>::id)
            .stringField(Trigger<*>::displayName)
            .build()
}