package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineDeploymentFinishStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineDeploymentFinishStatus : GQLType {

    override fun getTypeName(): String = SlotPipelineDeploymentFinishStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Status returned when finishing a deployment")
            .booleanField(SlotPipelineDeploymentFinishStatus::deployed)
            .stringField(SlotPipelineDeploymentFinishStatus::message)
            .build()
}