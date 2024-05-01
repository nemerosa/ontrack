package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanFieldFunction
import net.nemerosa.ontrack.graphql.support.enumField
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowInstance : GQLType {

    override fun getTypeName(): String = WorkflowInstance::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Running workflow instance")
            // Status
            .enumField(WorkflowInstance::status)
            // Finished (status)
            .booleanFieldFunction<WorkflowInstance>(
                name = "finished",
                description = "Is the workflow finished?",
            ) {
                it.status.finished
            }
            // OK
            .build()
}