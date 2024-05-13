package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowInstanceNode : GQLType {

    override fun getTypeName(): String = WorkflowInstanceNode::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Status of a node execution in a workflow")
            .stringField(WorkflowInstanceNode::id)
            .enumField(WorkflowInstanceNode::status)
            .jsonField(WorkflowInstanceNode::output)
            .stringField(WorkflowInstanceNode::error)
            .localDateTimeField(WorkflowInstanceNode::startTime)
            .localDateTimeField(WorkflowInstanceNode::endTime)
            .longField(WorkflowInstanceNode::durationMs)
            .build()
}