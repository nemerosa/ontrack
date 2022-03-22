package net.nemerosa.ontrack.extension.notifications.webhooks

interface WebhookAdminService {

    fun findWebhookByName(name: String): Webhook?

}