package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineAdmissionRuleStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanFieldFunction
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineAdmissionRuleStatus : GQLType {
    override fun getTypeName(): String = SlotPipelineAdmissionRuleStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Stored state for an admission rule in a pipeline")
            .stringField(SlotPipelineAdmissionRuleStatus::id)
            .field(SlotPipelineAdmissionRuleStatus::data)
            .field(SlotPipelineAdmissionRuleStatus::override)
            .booleanFieldFunction<SlotPipelineAdmissionRuleStatus>(
                "overridden",
                "Flag to check if the rule has been overridden"
            ) {
                it.override != null
            }
            .build()
}