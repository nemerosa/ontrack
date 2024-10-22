package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRule
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotAdmissionRule : GQLType {

    override fun getTypeName(): String = SlotAdmissionRule::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Admission rule")
            .stringField(SlotAdmissionRule<*, *>::id)
            .stringField(SlotAdmissionRule<*, *>::name)
            .build()
}