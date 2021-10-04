package net.nemerosa.ontrack.extension.github.app.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

/**
 * REST client to GitHub.
 *
 * See https://docs.github.com/en/rest/reference/apps
 */
class DefaultGitHubAppClient : GitHubAppClient {

    override fun getAppInstallations(jwt: String): List<GitHubAppInstallation> =
        client(jwt).getForObject("/app/installations", JsonNode::class.java)
            ?.map {
                it.parse()
            }
            ?: emptyList()

    override fun generateInstallationToken(jwt: String, appInstallationId: String): GitHubAppInstallationToken =
        client(jwt).postForObject(
            "/app/installations/$appInstallationId/access_tokens",
            null,
            GitHubAppInstallationToken::class.java
        )
            ?: throw GitHubAppClientCannotGetInstallationTokenException(appInstallationId)

    private fun client(jwt: String): RestTemplate = RestTemplateBuilder()
        .rootUri("https://api.github.com")
        .defaultHeader("Authorization", "Bearer $jwt")
        .build()

}