package net.nemerosa.ontrack.extension.environments.workflows

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.rootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySlotWorkflowInstanceById(
    private val slotWorkflowService: SlotWorkflowService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        rootQuery(
            name = "slotWorkflowInstanceById",
            description = "Getting a slot workflow instance by id",
            outputType = SlotWorkflowInstance::class,
            arguments = listOf(
                stringArgument("id", "Slot workflow instance if", nullable = false),
            )
        ) { env ->
            val id = env.getArgument<String>("id")
            slotWorkflowService.findSlotWorkflowInstanceById(id)
        }
}