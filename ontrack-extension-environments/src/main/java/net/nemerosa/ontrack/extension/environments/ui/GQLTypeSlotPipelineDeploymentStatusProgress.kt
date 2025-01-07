package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineDeploymentStatusProgress
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.intField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineDeploymentStatusProgress : GQLType {

    override fun getTypeName(): String = SlotPipelineDeploymentStatusProgress::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Progress for a deployment status")
            .booleanField(SlotPipelineDeploymentStatusProgress::ok)
            .booleanField(SlotPipelineDeploymentStatusProgress::overridden)
            .intField(SlotPipelineDeploymentStatusProgress::successCount)
            .intField(SlotPipelineDeploymentStatusProgress::totalCount)
            .intField(SlotPipelineDeploymentStatusProgress::percentage)
            .build()
}