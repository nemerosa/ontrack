package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowNodeExecutor : GQLType {

    override fun getTypeName(): String = WorkflowNodeExecutor::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Workflow node executor")
            .stringField(WorkflowNodeExecutor::id)
            .stringField(WorkflowNodeExecutor::displayName)
            .booleanField(WorkflowNodeExecutor::enabled)
            .build()
}