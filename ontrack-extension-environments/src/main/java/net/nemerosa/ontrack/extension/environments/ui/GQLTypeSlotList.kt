package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotList(
    private val gqlTypeSlot: GQLTypeSlot,
) : GQLType {
    override fun getTypeName(): String = SlotList::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Holds a list of slots")
            .field {
                it.name(SlotList::slots.name)
                    .description("Holds a list of slots")
                    .type(listType(gqlTypeSlot.typeRef))
            }
            .build()
}