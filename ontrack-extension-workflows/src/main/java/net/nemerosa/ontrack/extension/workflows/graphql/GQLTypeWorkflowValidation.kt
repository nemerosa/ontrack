package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringListField
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowValidation : GQLType {

    override fun getTypeName(): String = WorkflowValidation::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Workflow validation")
            .stringListField(WorkflowValidation::errors)
            .booleanField(WorkflowValidation::error)
            .build()
}