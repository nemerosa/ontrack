package net.nemerosa.ontrack.kdsl.connector

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import java.nio.charset.Charset

interface ConnectorResponseBody {

    fun asText(charset: Charset = Charsets.UTF_8): String

    fun asJson() = asText().parseAsJson()

}