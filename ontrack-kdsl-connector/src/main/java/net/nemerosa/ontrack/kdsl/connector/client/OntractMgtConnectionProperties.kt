package net.nemerosa.ontrack.kdsl.connector.client

/**
 * Defines what's needed to connect to Ontrack management endpoints.
 *
 * @property url Root URL of the Ontrack management endpoint (for example: http://localhost:8800/manage).
 * @property token API token (optional)
 */
class OntractMgtConnectionProperties(
    val url: String,
    val token: String?,
)
