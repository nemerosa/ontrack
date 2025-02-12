package net.nemerosa.ontrack.graphql.schema.trigger

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.trigger.Trigger
import net.nemerosa.ontrack.model.trigger.TriggerRegistry
import org.springframework.stereotype.Component

@Component
class GQLRootQueryTriggerList(
    private val triggerRegistry: TriggerRegistry,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("triggerList")
            .description("List of workflow instance statuses")
            .type(listType(Trigger::class.toTypeRef()))
            .dataFetcher {
                triggerRegistry.triggers
            }
            .build()
}
