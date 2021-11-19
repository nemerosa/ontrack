package net.nemerosa.ontrack.kdsl.connector

/**
 * Raw HTTP connection.
 */
interface Connector {

    /**
     * Associated URL
     */
    val url: String

    /**
     * Associated token, if any
     */
    val token: String?

    /**
     * Gets some content from a relative URL.
     */
    fun get(
        path: String,
        headers: Map<String, String> = emptyMap(),
    ): ConnectorResponse

    /**
     * Post a payload to a relative URL.
     */
    fun post(
        path: String,
        headers: Map<String, String> = emptyMap(),
        body: Any? = null,
    ): ConnectorResponse

}