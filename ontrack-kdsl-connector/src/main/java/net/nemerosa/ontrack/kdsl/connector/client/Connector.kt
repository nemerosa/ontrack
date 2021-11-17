package net.nemerosa.ontrack.kdsl.connector.client

/**
 * Raw HTTP connection.
 */
interface Connector {

    /**
     * Post a payload to a relative URL.
     */
    fun post(
        path: String,
        headers: Map<String,String> = emptyMap(),
        body: Any? = null,
    ): ConnectorResponse

}