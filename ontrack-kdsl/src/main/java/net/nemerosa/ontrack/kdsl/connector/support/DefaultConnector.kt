package net.nemerosa.ontrack.kdsl.connector.support

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponse
import net.nemerosa.ontrack.kdsl.connector.ConnectorResponseBody
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity
import java.nio.charset.Charset


class DefaultConnector(
    override val url: String,
    private val defaultHeaders: Map<String, String> = emptyMap(),
) : Connector {

    companion object {
        const val X_ONTRACK_TOKEN = "X-Ontrack-Token"
    }

    override val token: String?
        get() = defaultHeaders[X_ONTRACK_TOKEN]

    override fun get(
        path: String,
        headers: Map<String, String>,
        noAuth: Boolean,
    ): ConnectorResponse {
        val response = restTemplate(headers, noAuth).getForEntity<ByteArray>(path)
        return RestTemplateConnectorResponse(response)
    }

    class RestTemplateConnectorResponse(
        private val response: ResponseEntity<ByteArray>,
    ) : ConnectorResponse {
        override val statusCode: Int
            get() = response.statusCodeValue
        override val body: ConnectorResponseBody = object : ConnectorResponseBody {
            override fun asTextOrNull(charset: Charset): String? =
                    response.body?.toString(charset)
            override fun asText(charset: Charset): String =
                    asTextOrNull(charset) ?: ""
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

    override fun uploadFile(path: String, headers: Map<String, String>, file: Pair<String, ByteArray>) {
        val actualHeaders = headers.toMutableMap()
        actualHeaders["Content-Type"] = MediaType.MULTIPART_FORM_DATA.toString()

        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        val (fileName, fileBytes) = file
        body.add(fileName, ByteArrayResource(fileBytes))

        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body)

        restTemplate(actualHeaders).postForEntity(
            path,
            requestEntity,
            String::class.java
        )
    }

    private fun restTemplate(
        headers: Map<String, String>,
        noAuth: Boolean = false,
    ): RestTemplate {
        var builder = RestTemplateBuilder().rootUri(url)
        defaultHeaders.forEach { (name, value) ->
            if ((name != "Authorization" && name != X_ONTRACK_TOKEN) || !noAuth) {
                builder = builder.defaultHeader(name, value)
            }
        }
        headers.forEach { (name, value) ->
            builder = builder.defaultHeader(name, value)
        }
        return builder.build()
    }
}