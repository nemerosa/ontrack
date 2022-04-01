package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType

/**
 * Representation of a promotion run.
 *
 * @property connector Ontrack connector
 * @property id Promotion run ID
 * @property name Promotion run name
 * @property description Promotion run description
 */
class PromotionRun(
    connector: Connector,
    id: UInt,
    val description: String?,
) : ProjectEntity(connector, ProjectEntityType.PROMOTION_RUN, id)
