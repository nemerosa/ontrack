package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector

/**
 * Representation of a resource.
 */
abstract class Resource(connector: Connector) : Connected(connector)
