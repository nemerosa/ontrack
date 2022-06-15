package net.nemerosa.ontrack.kdsl.connector.client

/**
 * Defines what's needed to connect to Ontrack.
 *
 * @property url Root URL of the Ontrack server.
 * @property token API token
 * @property internalUrl Used for the internal test webhook to connect from Ontrack to Ontrack
 */
class OntractConnectionProperties(
    val url: String,
    val token: String,
    val internalUrl: String,
)
