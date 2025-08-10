package net.nemerosa.ontrack.gradle.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.StringEntity

class OntrackClient(
    private val url: String,
    private val token: String,
    private val logger: (message: String) -> Unit = {}
) {

    private val objectMapper = ObjectMapper()

    fun graphQL(
        query: String,
        variables: Map<String, Any?> = emptyMap()
    ): JsonNode {

        logger("Ontrack GraphQL Query: $query")
        logger("Ontrack GraphQL Variables: $variables")

        return HttpClients.createDefault().use { httpClient ->

            val requestBody = objectMapper.writeValueAsString(
                mapOf(
                    "query" to query,
                    "variables" to variables
                )
            )

            val httpPost = HttpPost("$url/graphql")
            httpPost.setHeader("X-Ontrack-Token", token)
            httpPost.setHeader("Accept", ContentType.APPLICATION_JSON)
            httpPost.entity = StringEntity(requestBody, ContentType.APPLICATION_JSON)

            httpClient.execute(httpPost) { response ->
                val statusCode = response.code
                if (statusCode == 200 && response.entity != null) {
                    val responseBody = response.entity.content.bufferedReader().use { it.readText() }
                    logger("Ontrack GraphQL Response: $responseBody")
                    val responseJson = objectMapper.readTree(responseBody)
                    responseJson.path("data")
                } else {
                    error("Request failed. Status code ${statusCode}. Reason: ${response.reasonPhrase}")
                }
            }

        }
    }

}