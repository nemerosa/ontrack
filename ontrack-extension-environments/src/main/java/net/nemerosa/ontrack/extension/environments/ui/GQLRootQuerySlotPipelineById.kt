package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySlotPipelineById(
    private val gqlTypeSlotPipeline: GQLTypeSlotPipeline,
    private val slotService: SlotService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("slotPipelineById")
            .description("Getting a slot pipeline by ID")
            .type(gqlTypeSlotPipeline.typeRef)
            .argument(stringArgument("id", "Pipeline ID", nullable = false))
            .dataFetcher { env ->
                val id: String = env.getArgument("id")
                slotService.findPipelineById(id)
            }
            .build()
}