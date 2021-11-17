package net.nemerosa.ontrack.kdsl.connector.client

import java.nio.charset.Charset

interface ConnectorResponseBody {

    fun asText(charset: Charset = Charsets.UTF_8): String

}