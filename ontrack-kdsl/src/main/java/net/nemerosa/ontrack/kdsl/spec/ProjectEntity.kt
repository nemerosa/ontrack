package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector

/**
 * Representation of a project entity.
 *
 * @property id Entity ID
 */
abstract class ProjectEntity(
    connector: Connector,
    id: UInt,
) : Entity(connector, id)
