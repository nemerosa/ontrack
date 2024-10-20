package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotAdmissionRuleConfig : GQLType {

    override fun getTypeName(): String = SlotAdmissionRuleConfig::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Configured admission rule for a slot")
            .stringField(SlotAdmissionRuleConfig::id)
            .stringField(SlotAdmissionRuleConfig::name)
            .stringField(SlotAdmissionRuleConfig::description)
            .stringField(SlotAdmissionRuleConfig::ruleId)
            .jsonField(SlotAdmissionRuleConfig::ruleConfig)
            .build()
}