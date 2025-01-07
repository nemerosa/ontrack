package net.nemerosa.ontrack.extension.environments.workflows

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotWorkflow(
    private val slotService: SlotService,
    private val slotWorkflowService: SlotWorkflowService,
) : GQLType {

    override fun getTypeName(): String = SlotWorkflow::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Workflow registered in a slot")
            .stringField(SlotWorkflow::id)
            .field(SlotWorkflow::slot)
            .enumField(SlotWorkflow::trigger)
            .field(SlotWorkflow::workflow)
            // Instance for a given pipeline
            .fieldGetter<SlotWorkflow, SlotWorkflowInstance>(
                name = "slotWorkflowInstanceForPipeline",
                description = "Getting the instance for a given pipeline",
                nullable = true,
                arguments = listOf(
                    stringArgument("pipelineId", "ID of the pipeline", nullable = false),
                )
            ) { workflow, env ->
                val pipelineId: String = env.getArgument("pipelineId")
                val pipeline = slotService.getPipelineById(pipelineId)
                slotWorkflowService.findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
                    pipeline = pipeline,
                    slotWorkflow = workflow,
                )
            }
            // OK
            .build()
}