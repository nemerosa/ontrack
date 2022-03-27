package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObject
import org.springframework.stereotype.Component

@Component
class GQLInputEventSubscriptionFilter : GQLInputType<EventSubscriptionFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(EventSubscriptionFilter::class, dictionary)

    override fun convert(argument: Any?): EventSubscriptionFilter? =
        asObject(
            argument,
            EventSubscriptionFilter::class.java
        )

    override fun getTypeRef() = GraphQLTypeReference(EventSubscriptionFilter::class.java.simpleName)
}