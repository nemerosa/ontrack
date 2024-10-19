package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.SlotPipelineAdmissionRuleStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.localDateTimeField
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
            .localDateTimeField(SlotPipelineAdmissionRuleStatus::timestamp)
            .stringField(SlotPipelineAdmissionRuleStatus::user)
            .jsonField(SlotPipelineAdmissionRuleStatus::data)
            .booleanField(SlotPipelineAdmissionRuleStatus::override)
            .stringField(SlotPipelineAdmissionRuleStatus::overrideMessage)
            .build()
}