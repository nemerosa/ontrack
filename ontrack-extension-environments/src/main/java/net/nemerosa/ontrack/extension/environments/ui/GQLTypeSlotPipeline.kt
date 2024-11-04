package net.nemerosa.ontrack.extension.environments.ui

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleInput
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineChange
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowInstance
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipeline(
    private val gqlTypeSlotPipelineDeploymentStatus: GQLTypeSlotPipelineDeploymentStatus,
    private val gqlTypeSlotPipelineChange: GQLTypeSlotPipelineChange,
    private val slotService: SlotService,
    private val slotWorkflowService: SlotWorkflowService,
) : GQLType {

    override fun getTypeName(): String = SlotPipeline::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Pipeline for a slot")
            .stringField(SlotPipeline::id)
            .intField(SlotPipeline::number)
            .localDateTimeField(SlotPipeline::start)
            .localDateTimeField(SlotPipeline::end)
            .enumField(SlotPipeline::status)
            .field(SlotPipeline::slot)
            .field(SlotPipeline::build)
            // Is the pipeline running?
            .field {
                it.name("finished")
                    .description("Is the pipeline finished?")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val pipeline: SlotPipeline = env.getSource()
                        pipeline.status.finished
                    }
            }
            // Deployment status
            .field {
                it.name("deploymentStatus")
                    .description("Deployment status for the pipeline")
                    .type(gqlTypeSlotPipelineDeploymentStatus.typeRef)
                    .dataFetcher { env ->
                        val pipeline: SlotPipeline = env.getSource()
                        slotService.startDeployment(
                            pipeline = pipeline,
                            dryRun = true,
                        )
                    }
            }
            // Last change
            .field {
                it.name("lastChange")
                    .description("Last change having occurred to the pipeline")
                    .type(gqlTypeSlotPipelineChange.typeRef)
                    .dataFetcher { env ->
                        val pipeline: SlotPipeline = env.getSource()
                        slotService.getPipelineChanges(pipeline).firstOrNull()
                    }
            }
            // All changes
            .listFieldGetter<SlotPipeline, SlotPipelineChange>(
                name = "changes",
                description = "Changes having occurred to the pipeline"
            ) { pipeline ->
                slotService.getPipelineChanges(pipeline)
            }
            // Required inputs
            .listFieldGetter<SlotPipeline, SlotAdmissionRuleInput>(
                name = "requiredInputs",
                description = "List of required inputs for the admission rules"
            ) { pipeline ->
                slotService.getRequiredInputs(pipeline)
            }
            // List of workflow instances
            .listFieldGetter<SlotPipeline, SlotWorkflowInstance>(
                name = "slotWorkflowInstances",
                description = "List of workflow instances for this pipeline",
            ) { pipeline ->
                slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline)
            }
            // OK
            .build()
}