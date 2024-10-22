package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineDeploymentCheck
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineDeploymentCheck : GQLType {

    override fun getTypeName(): String = SlotPipelineDeploymentCheck::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of checks for admission for a pipeline")
            .field(SlotPipelineDeploymentCheck::check)
            .stringField(SlotPipelineDeploymentCheck::ruleId)
            .jsonField(SlotPipelineDeploymentCheck::ruleConfig)
            .jsonField(SlotPipelineDeploymentCheck::ruleData)
            .field(SlotPipelineDeploymentCheck::override)
            .build()
}