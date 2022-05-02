package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.json.asJson

object WebhookFixtures {

    fun webhookAuthentication() = WebhookAuthentication(
        type = "header",
        config = mapOf(
            "name" to "X-Ontrack-Token",
            "value" to "xxxx",
        ).asJson()
    )

}