package net.nemerosa.ontrack.kdsl.connector.support

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponse
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponseBody
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import java.nio.charset.Charset

class DefaultConnector(
    private val url: String,
    private val defaultHeaders: Map<String, String>,
) : Connector {

    override fun get(path: String, headers: Map<String, String>): ConnectorResponse {
        val response = restTemplate(headers).getForEntity<ByteArray>(path)
        return RestTemplateConnectorResponse(response)
    }

    class RestTemplateConnectorResponse(
        private val response: ResponseEntity<ByteArray>,
    ) : ConnectorResponse {
        override val statusCode: Int
            get() = response.statusCodeValue
        override val body: ConnectorResponseBody = object : ConnectorResponseBody {
            override fun asText(charset: Charset): String =
                response.body?.toString(charset) ?: ""
        }
    }

    override fun post(path: String, headers: Map<String, String>, body: Any?): ConnectorResponse {
        TODO("Not yet implemented")
    }

    private fun restTemplate(
        headers: Map<String, String>,
    ): RestTemplate =
        RestTemplateBuilder()
            .rootUri(url)
            .apply {
                defaultHeaders.forEach { (name, value) ->
                    defaultHeader(name, value)
                }
                headers.forEach { (name, value) ->
                    defaultHeader(name, value)
                }
            }
            .build()
}