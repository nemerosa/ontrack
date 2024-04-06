package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.events.EventType
import org.springframework.stereotype.Component

@Component
class GQLTypeEventType : GQLType {

    override fun getTypeName(): String = EventType::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(EventType::class))
            .stringField(EventType::id)
            .stringField(EventType::description)
            .build()
}