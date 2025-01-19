package net.nemerosa.ontrack.extension.environments.workflows

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotDeploymentCheck
import net.nemerosa.ontrack.extension.environments.security.SlotPipelineOverride
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotWorkflowInstance(
    private val slotWorkflowService: SlotWorkflowService,
    private val securityService: SecurityService,
) : GQLType {

    override fun getTypeName(): String = SlotWorkflowInstance::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Running instance of a workflow for a pipeline")
            .stringField(SlotWorkflowInstance::id)
            .localDateTimeField(SlotWorkflowInstance::start)
            .field(SlotWorkflowInstance::pipeline)
            .field(SlotWorkflowInstance::slotWorkflow)
            .field(SlotWorkflowInstance::workflowInstance)
            .field(SlotWorkflowInstance::override)
            .field {
                it.name("check")
                    .description("Status check for this workflow")
                    .type(SlotDeploymentCheck::class.toTypeRef().toNotNull())
                    .dataFetcher { env ->
                        val slotWorkflowInstance: SlotWorkflowInstance = env.getSource()
                        slotWorkflowService.getSlotWorkflowCheck(
                            pipeline = slotWorkflowInstance.pipeline,
                            slotWorkflow = slotWorkflowInstance.slotWorkflow,
                        )
                    }
            }
            .booleanFieldFunction<SlotWorkflowInstance>(
                "overridden",
                "Flag to check if the workflow has been overridden"
            ) {
                it.override != null
            }
            .booleanFieldFunction<SlotWorkflowInstance>(
                "canBeOverridden",
                "True if the user is allowed to override this rule"
            ) {
                securityService.isProjectFunctionGranted(
                    it.pipeline.slot.project,
                    SlotPipelineOverride::class.java
                )
            }
            .build()
}