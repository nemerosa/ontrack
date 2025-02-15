package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowInstanceContextData : GQLType {

    override fun getTypeName(): String = "WorkflowInstanceContextData"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(WorkflowInstanceContextData::class))
            .stringField(WorkflowInstanceContextData::name)
            .field(WorkflowInstanceContextData::contextData)
            .build()
}