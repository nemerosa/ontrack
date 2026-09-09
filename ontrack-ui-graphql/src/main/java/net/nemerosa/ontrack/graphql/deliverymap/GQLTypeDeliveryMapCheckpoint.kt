package net.nemerosa.ontrack.graphql.deliverymap

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpoint
import org.springframework.stereotype.Component

@Component
class GQLTypeDeliveryMapCheckpoint : GQLType {

    override fun getTypeName(): String = DeliveryMapCheckpoint::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("One node of a delivery map")
            .stringField(DeliveryMapCheckpoint::id)
            .stringField(DeliveryMapCheckpoint::type)
            .stringField(DeliveryMapCheckpoint::name)
            .stringField(DeliveryMapCheckpoint::description)
            .jsonField(DeliveryMapCheckpoint::data)
            .field(DeliveryMapCheckpoint::arrival)
            .listField(DeliveryMapCheckpoint::members)
            .build()
}
