package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleInputField
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotAdmissionRuleInputField : GQLType {
    override fun getTypeName(): String = SlotAdmissionRuleInputField::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Input for an admission rule")
            .enumField(SlotAdmissionRuleInputField::type)
            .stringField(SlotAdmissionRuleInputField::name)
            .stringField(SlotAdmissionRuleInputField::label)
            .jsonField(SlotAdmissionRuleInputField::value)
            .build()
}