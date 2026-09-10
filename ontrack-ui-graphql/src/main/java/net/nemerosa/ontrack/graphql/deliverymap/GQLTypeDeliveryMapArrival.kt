package net.nemerosa.ontrack.graphql.deliverymap

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.localDateTimeField
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapArrival
import org.springframework.stereotype.Component

@Component
class GQLTypeDeliveryMapArrival : GQLType {

    override fun getTypeName(): String = DeliveryMapArrival::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Latest build to have arrived at a checkpoint, and what became of it there")
            .field(DeliveryMapArrival::build)
            .localDateTimeField(DeliveryMapArrival::time)
            .field(DeliveryMapArrival::status)
            .intField(DeliveryMapArrival::lag)
            .build()
}
