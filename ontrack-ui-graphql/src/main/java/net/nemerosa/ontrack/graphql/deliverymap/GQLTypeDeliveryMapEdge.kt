package net.nemerosa.ontrack.graphql.deliverymap

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdge
import org.springframework.stereotype.Component

@Component
class GQLTypeDeliveryMapEdge : GQLType {

    override fun getTypeName(): String = DeliveryMapEdge::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("A dependency between two checkpoints of a delivery map")
            .stringField(DeliveryMapEdge::id)
            .enumField(DeliveryMapEdge::kind)
            .stringField(DeliveryMapEdge::source)
            .stringField(DeliveryMapEdge::target)
            .build()
}
