package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.events.EventFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEventTypes(
    private val gqlTypeEventType: GQLTypeEventType,
    private val eventFactory: EventFactory,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("eventTypes")
            .description("List of all event types")
            .type(listType(gqlTypeEventType.typeRef))
            .dataFetcher {
                eventFactory.eventTypes.sortedBy { it.id }
            }
            .build()
}