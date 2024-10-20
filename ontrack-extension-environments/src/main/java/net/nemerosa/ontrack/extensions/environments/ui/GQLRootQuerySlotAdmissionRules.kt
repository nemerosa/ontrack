package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extensions.environments.rules.SlotAdmissionRuleRegistry
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySlotAdmissionRules(
    private val gqlTypeSlotAdmissionRule: GQLTypeSlotAdmissionRule,
    private val slotAdmissionRuleRegistry: SlotAdmissionRuleRegistry,
): GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("slotAdmissionRules")
            .description("List of existing admission rules")
            .type(listType(gqlTypeSlotAdmissionRule.typeRef))
            .dataFetcher {
                slotAdmissionRuleRegistry.rules
            }
            .build()
}