package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType

/**
 * Representation of a project entity.
 *
 * @property id Entity ID
 */
abstract class ProjectEntity(
    connector: Connector,
    val type: ProjectEntityType,
    id: UInt,
) : Entity(connector, id)
