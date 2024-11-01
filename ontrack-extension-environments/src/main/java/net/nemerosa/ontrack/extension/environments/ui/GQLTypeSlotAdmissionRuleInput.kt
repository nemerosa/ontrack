package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleInput
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotAdmissionRuleInput : GQLType {

    override fun getTypeName(): String = SlotAdmissionRuleInput::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Input for an admission rule")
            .field(SlotAdmissionRuleInput::config)
            .build()
}