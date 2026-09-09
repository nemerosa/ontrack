package net.nemerosa.ontrack.graphql.deliverymap

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapEdgeKind
import org.springframework.stereotype.Component

@Component
class GQLEnumDeliveryMapEdgeKind : AbstractGQLEnum<DeliveryMapEdgeKind>(
    DeliveryMapEdgeKind::class,
    DeliveryMapEdgeKind.values(),
    "What an edge of a delivery map means"
)
