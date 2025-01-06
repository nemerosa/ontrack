package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleOverride
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotAdmissionRuleOverride : GQLType {

    override fun getTypeName(): String = SlotAdmissionRuleOverride::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getAPITypeDescription(SlotAdmissionRuleOverride::class))
            .stringField(SlotAdmissionRuleOverride::user)
            .localDateTimeField(SlotAdmissionRuleOverride::timestamp)
            .jsonField(SlotAdmissionRuleOverride::message)
            .build()
}