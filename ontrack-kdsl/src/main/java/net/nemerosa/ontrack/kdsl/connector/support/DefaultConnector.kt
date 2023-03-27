package net.nemerosa.ontrack.kdsl.connector.support

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponse
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponseBody
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity
import java.nio.charset.Charset

class DefaultConnector(
    override val url: String,
    private val defaultHeaders: Map<String, String> = emptyMap(),
) : Connector {

    override val token: String?
        get() = defaultHeaders["X-Ontrack-Token"]

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
        val response = restTemplate(headers).postForEntity<ByteArray>(path, body)
        return RestTemplateConnectorResponse(response)
    }

    override fun put(path: String, headers: Map<String, String>, body: Any?) {
        restTemplate(headers).put(path, body)
    }

    override fun delete(path: String, headers: Map<String, String>) {
        restTemplate(headers).delete(path)
    }

    private fun restTemplate(
        headers: Map<String, String>,
    ): RestTemplate {
        var builder = RestTemplateBuilder().rootUri(url)
        defaultHeaders.forEach { (name, value) ->
            builder = builder.defaultHeader(name, value)
        }
        headers.forEach { (name, value) ->
            builder = builder.defaultHeader(name, value)
        }
        return builder.build()
    }
}