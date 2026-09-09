package net.nemerosa.ontrack.graphql.deliverymap

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.model.deliverymap.DeliveryMap
import org.springframework.stereotype.Component

@Component
class GQLTypeDeliveryMap : GQLType {

    override fun getTypeName(): String = DeliveryMap::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("What a build on a branch has to pass through on its way to an environment")
            .listField(DeliveryMap::checkpoints)
            .listField(DeliveryMap::edges)
            .build()
}
