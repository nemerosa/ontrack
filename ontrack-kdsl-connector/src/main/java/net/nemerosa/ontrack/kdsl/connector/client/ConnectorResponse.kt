package net.nemerosa.ontrack.kdsl.connector.client

interface ConnectorResponse {

    val statusCode: Int

    val body: ConnectorResponseBody

}