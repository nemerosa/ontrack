package net.nemerosa.ontrack.gradle.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

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

        val httpClient = HttpClientBuilder.create().build()
        httpClient.use {
            val requestBody = objectMapper.writeValueAsString(
                    mapOf(
                            "query" to query,
                            "variables" to variables
                    )
            )

            val httpPost = HttpPost("$url/graphql")
            httpPost.setHeader("X-Ontrack-Token", token)
            httpPost.entity = StringEntity(requestBody, ContentType.APPLICATION_JSON)

            val response = httpClient.execute(httpPost)
            val responseEntity: HttpEntity? = response.entity

            if (response.statusLine.statusCode == 200 && responseEntity != null) {
                val responseBody = EntityUtils.toString(responseEntity)
                logger("Ontrack GraphQL Response: $responseBody")
                val responseJson = objectMapper.readTree(responseBody)
                return responseJson.path("data")
            } else {
                error("Request failed. Status code ${response.statusLine.statusCode}. Reason: ${response.statusLine.reasonPhrase}")
            }
        }
    }

}