package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEventSubscriptions(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeEventSubscriptionPayload: GQLTypeEventSubscriptionPayload,
    private val gqlInputEventSubscriptionFilter: GQLInputEventSubscriptionFilter,
    private val eventSubscriptionService: EventSubscriptionService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, EventSubscriptionPayload>(
            cache = GQLTypeCache(),
            fieldName = "eventSubscriptions",
            fieldDescription = "List of event subscriptions",
            itemType = gqlTypeEventSubscriptionPayload,
            arguments = listOf(
                GraphQLArgument.newArgument()
                    .name("filter")
                    .description("Filter for the subscriptions")
                    .type(gqlInputEventSubscriptionFilter.typeRef)
                    .build()
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                // Parsing of the filter
                val filter = env.getArgument<Any>("filter").asJson().parse<EventSubscriptionFilter>()
                    // Pagination from the root arguments
                    .withPage(offset, size)
                // Getting the list
                eventSubscriptionService.filterSubscriptions(filter).map {
                    EventSubscriptionPayload(
                        id = it.id,
                        channel = it.data.channel,
                        channelConfig = it.data.channelConfig,
                        events = it.data.events.toList(),
                        keywords = it.data.keywords,
                    )
                }
            }
        )

}