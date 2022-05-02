package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType

/**
 * Representation of a promotion level.
 *
 * @property connector Ontrack connector
 * @property id Promotion level ID
 * @property name Promotion level name
 * @property description Promotion level description
 */
class PromotionLevel(
    connector: Connector,
    id: UInt,
    val name: String,
    val description: String?,
) : ProjectEntity(connector, ProjectEntityType.PROMOTION_LEVEL, id)
