package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineDeploymentStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineDeploymentStatus : GQLType {

    override fun getTypeName(): String = SlotPipelineDeploymentStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Status for the admission check of a pipeline")
            .listField(SlotPipelineDeploymentStatus::checks)
            .booleanField(SlotPipelineDeploymentStatus::status)
            .booleanField(SlotPipelineDeploymentStatus::override)
            .build()
}