package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.service.graph.SlotNode
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotNode : GQLType {

    override fun getTypeName(): String = SlotNode::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Node for a slot in a graph")
            .field(SlotNode::slot)
            .listField(SlotNode::parents)
            .build()
}