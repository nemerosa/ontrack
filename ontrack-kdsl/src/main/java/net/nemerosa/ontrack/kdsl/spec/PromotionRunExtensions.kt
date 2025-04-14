package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.PromotionRunFragment

/**
 * Creates a [PromotionRun] from a GraphQL [PromotionRunFragment].
 */
fun PromotionRunFragment.toPromotionRun(connected: Connected) = PromotionRun(
    connector = connected.connector,
    id = id.toUInt(),
    description = description,
)
