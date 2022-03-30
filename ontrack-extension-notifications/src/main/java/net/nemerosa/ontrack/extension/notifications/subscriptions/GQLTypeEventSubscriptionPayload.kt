package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeEventSubscriptionPayload : GQLType {
    override fun getTypeName(): String = EventSubscriptionPayload::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description(getDescription(EventSubscriptionPayload::class))
        .stringField(EventSubscriptionPayload::id)
        .stringField(EventSubscriptionPayload::channel)
        .field {
            it.name(EventSubscriptionPayload::channelConfig.name)
                .description(getPropertyDescription(EventSubscriptionPayload::channelConfig))
                .type(GQLScalarJSON.INSTANCE.toNotNull())
        }
        .field {
            it.name(EventSubscriptionPayload::events.name)
                .description(getPropertyDescription(EventSubscriptionPayload::events))
                .type(listType(GraphQLString))
        }
        .stringField(EventSubscriptionPayload::keywords)
        .build()

}