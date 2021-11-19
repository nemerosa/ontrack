package net.nemerosa.ontrack.kdsl.connector

interface ConnectorResponse {

    val statusCode: Int

    val body: ConnectorResponseBody

}