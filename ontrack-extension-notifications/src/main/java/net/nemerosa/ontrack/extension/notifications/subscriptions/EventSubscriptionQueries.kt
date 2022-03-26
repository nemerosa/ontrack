package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEventSubscriptions(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeEventSubscriptionPayload: GQLTypeEventSubscriptionPayload,
    private val eventSubscriptionService: EventSubscriptionService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, SavedEventSubscription>(
            cache = GQLTypeCache(),
            fieldName = "eventSubscriptions",
            fieldDescription = "List of event subscriptions",
            itemType = gqlTypeEventSubscriptionPayload,
            arguments = listOf(
                // TODO Filtering
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                // Creating the filter
                val filter = EventSubscriptionFilter(
                    offset = offset,
                    size = size,
                )
                // Getting the list
                eventSubscriptionService.filterSubscriptions(filter)
            }
        )

}