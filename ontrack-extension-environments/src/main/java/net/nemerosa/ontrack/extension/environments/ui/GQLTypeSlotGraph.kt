package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.service.graph.SlotGraph
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotGraph : GQLType {
    override fun getTypeName(): String = SlotGraph::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Graph of slots for a project")
            .listField(SlotGraph::slotNodes)
            .build()
}