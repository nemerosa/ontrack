package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySlotById(
    private val gqlTypeSlot: GQLTypeSlot,
    private val slotService: SlotService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("slotById")
            .description("Getting a slot using its id")
            .type(gqlTypeSlot.typeRef)
            .argument(stringArgument(name = "id", description = "ID of the slot", nullable = false))
            .dataFetcher { env ->
                val id: String = env.getArgument("id")
                slotService.getSlotById(id)
            }
            .build()
}