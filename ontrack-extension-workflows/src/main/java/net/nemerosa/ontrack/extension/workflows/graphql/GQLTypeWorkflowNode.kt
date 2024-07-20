package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.longField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowNode : GQLType {

    override fun getTypeName(): String = WorkflowNode::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Workflow node")
            .stringField(WorkflowNode::id)
            .stringField(WorkflowNode::description)
            .stringField(WorkflowNode::executorId)
            .longField(WorkflowNode::timeout)
            .jsonField(WorkflowNode::data)
            .listField(WorkflowNode::parents)
            .build()
}