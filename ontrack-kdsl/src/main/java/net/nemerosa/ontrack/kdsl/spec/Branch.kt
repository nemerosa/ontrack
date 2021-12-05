package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector

/**
 * Representation of a branch.
 *
 * @property connector Ontrack connector
 * @property id Branch ID
 * @property name Branch name
 * @property description Branch description
 * @property disabled Branch state
 */
class Branch(
    connector: Connector,
    id: UInt,
    val name: String,
    val description: String?,
    val disabled: Boolean,
) : ProjectEntity(connector, id)
