package net.nemerosa.ontrack.extension.notifications.webhooks

import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

@Service
class DefaultWebhookExecutionService(
    private val webhookPayloadRenderer: WebhookPayloadRenderer,
    private val webhookAuthenticatorRegistry: WebhookAuthenticatorRegistry,
) : WebhookExecutionService {

    override fun send(webhook: Webhook, payload: WebhookPayload<*>) {
        val client: HttpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

        val request: HttpRequest =
            HttpRequest.newBuilder()
                .uri(URI.create(webhook.url))
                // TODO Gets the maximum timeout between the webhook one and the general settings
                .timeout(webhook.timeout)
                .header("Content-Type", "application/json")
                // Authentication
                .apply {
                    authenticate(webhook, this)
                }
                // Payload
                .POST(BodyPublishers.ofByteArray(webhookPayloadRenderer.render(payload)))
                // OK
                .build()

        client.sendAsync(request, BodyHandlers.ofString())
            .thenApply { response ->
                // TODO Do we need to check the status code here?
                response.body()
            }
            .thenAccept { response ->
                TODO("Logs the response and other meta information")
            }
    }

    private fun authenticate(webhook: Webhook, builder: HttpRequest.Builder) {
        val authenticator = webhookAuthenticatorRegistry.findWebhookAuthenticator(webhook.authentication.type)
            ?: throw WebhookAuthenticatorNotFoundException(webhook.authentication.type)
        authenticate(webhook, authenticator, builder)
    }

    private fun <C> authenticate(
        webhook: Webhook,
        authenticator: WebhookAuthenticator<C>,
        builder: HttpRequest.Builder,
    ) {
        val config = authenticator.validateConfig(webhook.authentication.config)
        authenticator.authenticate(config, builder)
    }

}