package net.nemerosa.ontrack.extension.environments.workflows

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotWorkflow : GQLType {

    override fun getTypeName(): String = SlotWorkflow::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Workflow registered in a slot")
            .stringField(SlotWorkflow::id)
            .field(SlotWorkflow::slot)
            .enumField(SlotWorkflow::trigger)
            .field(SlotWorkflow::workflow)
            .build()
}