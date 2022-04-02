package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.webhooks

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.seconds
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks.WebhookSettings
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks.webhooks
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import org.junit.jupiter.api.Test
import java.time.Duration

@AcceptanceTestSuite
class ACCDSLWebhooks : AbstractACCDSLNotificationsTestSupport() {

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
                waitUntil(timeout = 10.seconds, interval = 500) {
                    ontrack.notifications.webhooks.internalEndpoint.payloads.any {
                        it.type == "event" &&
                                it.data.path("eventType").path("id").asText() == "new_branch"
                        // TODO Checks the exact event
                    }
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

}