package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlot : GQLType {
    override fun getTypeName(): String = Slot::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Deployment slot into an environment")
            .stringField(Slot::id)
            .stringField(Slot::description)
            .field(Slot::project)
            .stringField(Slot::qualifier)
            .field(Slot::environment)
            .build()
}