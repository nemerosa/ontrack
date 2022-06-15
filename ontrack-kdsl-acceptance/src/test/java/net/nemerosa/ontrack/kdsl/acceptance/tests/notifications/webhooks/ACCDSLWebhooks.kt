package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.webhooks

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.seconds
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks.*
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration

class ACCDSLWebhooks : AbstractACCDSLNotificationsTestSupport() {

    companion object {
        private const val injectionWebhookCount = 3
        private const val injectionWebhookRolls = 30
    }

    /**
     * Registers several webhooks pointing to the internal webhook endpoint (IWE).
     *
     * Send "test" messages on the IWE which send "test" payloads to the webhook.
     */
    @Test
    @Disabled("Not really a test")
    fun `Generating exchanges`() {
        withWebhooksEnabled {
            repeat(injectionWebhookCount) {
                // Creates an internal webhook
                val webhookName = uid("whi")
                ontrack.notifications.webhooks.createWebhook(
                    name = webhookName,
                    enabled = true,
                    url = "${ontractConnectionProperties.url}/extension/notifications/webhooks/internal",
                    timeout = Duration.ofMinutes(1),
                    authenticationType = "header",
                    authenticationConfig = mapOf(
                        "name" to "X-Ontrack-Token",
                        "value" to ontractConnectionProperties.token,
                    )
                )
                // Sends all kinds of notifications to the internal end point
                repeat(injectionWebhookRolls) {
                    ontrack.notifications.webhooks.internalEndpoint.testOk(
                        webhookName,
                        "OK $it"
                    )
                    ontrack.notifications.webhooks.internalEndpoint.testOk(
                        webhookName,
                        "OK $it with delay",
                        delayMs = 1000L,
                    )
                    ontrack.notifications.webhooks.internalEndpoint.testNotFound(
                        webhookName,
                        "Not found $it"
                    )
                    ontrack.notifications.webhooks.internalEndpoint.testError(
                        webhookName,
                        "Error $it"
                    )
                }
            }
        }
    }

    @Test
    fun `Registering and using a webhook`() {
        withWebhooksEnabled {
            // Creates a webhook
            val webhookName = uid("wh")
            ontrack.notifications.webhooks.createWebhook(
                name = webhookName,
                enabled = true,
                url = "${ontractConnectionProperties.url}/extension/notifications/webhooks/internal",
                timeout = Duration.ofMinutes(1),
                authenticationType = "header",
                authenticationConfig = mapOf(
                    "name" to "X-Ontrack-Token",
                    "value" to ontractConnectionProperties.token,
                )
            )
            // For a project
            project {
                // Subscribe for events on this project,
                // sending the events to the webhook
                subscribe(
                    channel = "webhook",
                    channelConfig = mapOf(
                        "name" to webhookName
                    ),
                    keywords = null,
                    events = listOf("new_branch"),
                )
                // Creating a new branch
                val branch = branch { this }
                // Checking the webhook has received the payload
                waitUntil(
                    timeout = 10.seconds, interval = 500,
                    task = "Checking the webhook has received the payload with event_type=new_branch, project=$name, branch=${branch.name}",
                    onTimeout = onTimeout(webhookName),
                ) {
                    ontrack.notifications.webhooks.internalEndpoint.payloads.any {
                        it.type == "event" &&
                                it.data.path("eventType").path("id").asText() == "new_branch"
                                && it.data.path("entities").path("PROJECT").path("name").asText() == name
                                && it.data.path("entities").path("BRANCH").path("name").asText() == branch.name
                    }
                }
            }
        }
    }

    @Test
    fun `Pinging a webhook`() {
        withWebhooksEnabled {
            // Creates a webhook
            val webhookName = uid("wh")
            ontrack.notifications.webhooks.createWebhook(
                name = webhookName,
                enabled = true,
                url = "${ontractConnectionProperties.url}/extension/notifications/webhooks/internal",
                timeout = Duration.ofMinutes(1),
                authenticationType = "header",
                authenticationConfig = mapOf(
                    "name" to "X-Ontrack-Token",
                    "value" to ontractConnectionProperties.token,
                )
            )
            // Pinging the webhook
            ontrack.notifications.webhooks.ping(webhookName)
            // Checks that the webhook received the event
            waitUntil(
                timeout = 10.seconds, interval = 500,
                task = "Checking the webhook has received the payload with type=type, message=Webhook $webhookName ping",
                onTimeout = onTimeout(webhookName),
            ) {
                val payloads = ontrack.notifications.webhooks.internalEndpoint.payloads
                payloads.any {
                    it.type == "ping"
                            && it.data.path("message").asText() == "Webhook $webhookName ping"
                }
            }
            // Checks that the delivery has been registered
            waitUntil(
                timeout = 10.seconds, interval = 500,
                task = "Checking the webhook delivery has been registered",
                onTimeout = onTimeout(webhookName),
            ) {
                val items = ontrack.notifications.webhooks.getDeliveries(webhook = webhookName).items
                items.any {
                    it.request.type == "ping"
                            && it.response?.code == 200
                            && it.response?.payload?.parseAsJson() == mapOf(
                        "ping" to mapOf(
                            "message" to "Webhook $webhookName ping"
                        )
                    ).asJson()
                }
            }
        }
    }

    private fun withWebhooksEnabled(code: () -> Unit) {
        val old = ontrack.settings.webhooks.get()
        try {
            ontrack.settings.webhooks.set(WebhookSettings(enabled = true))
            code()
        } finally {
            ontrack.settings.webhooks.set(old)
        }
    }

    private fun onTimeout(webhookName: String): () -> Unit = {
        // Details about the webhook
        val webhook = ontrack.notifications.webhooks.findWebhookByName(webhookName)
        println("$webhookName webhook details:")
        println(webhook)
        // Get the last deliveries of the webhook
        println("$webhookName webhook last deliveries:")
        val deliveries = ontrack.notifications.webhooks.getDeliveries(webhook = webhookName).items
        deliveries.forEach { delivery ->
            println("----------------------------")
            println(delivery)
        }
    }

}