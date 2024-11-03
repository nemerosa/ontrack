package net.nemerosa.ontrack.extension.environments.workflows

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotWorkflowInstance : GQLType {

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
            .build()
}