package net.nemerosa.ontrack.extension.notifications.webhooks

import java.time.Duration

interface WebhookAdminService {

    fun findWebhookByName(name: String): Webhook?

    fun createWebhook(
        name: String,
        enabled: Boolean,
        url: String,
        timeout: Duration,
    ): Webhook

}