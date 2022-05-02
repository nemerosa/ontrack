package net.nemerosa.ontrack.extension.notifications.webhooks

import java.time.Duration

interface WebhookAdminService {

    /**
     * Gets the list of all webhooks
     */
    val webhooks: List<Webhook>

    fun findWebhookByName(name: String): Webhook?

    fun createWebhook(
        name: String,
        enabled: Boolean,
        url: String,
        timeout: Duration,
        authentication: WebhookAuthentication,
    ): Webhook

    fun updateWebhook(
        name: String,
        enabled: Boolean? = null,
        url: String? = null,
        timeout: Duration? = null,
        authentication: WebhookAuthentication? = null,
    ): Webhook

    fun deleteWebhook(name: String)

}