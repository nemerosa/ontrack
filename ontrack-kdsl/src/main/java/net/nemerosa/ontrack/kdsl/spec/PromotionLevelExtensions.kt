package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.PromotionLevelFragment

/**
 * Creates a [PromotionLevel] from a GraphQL [PromotionLevelFragment].
 */
fun PromotionLevelFragment.toPromotionLevel(connected: Connected) = PromotionLevel(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
)
