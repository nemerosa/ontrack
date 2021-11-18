package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector

/**
 * Representation of an entity.
 *
 * @property id Entity ID
 */
abstract class Entity(
    connector: Connector,
    val id: UInt,
) : Resource(connector)
