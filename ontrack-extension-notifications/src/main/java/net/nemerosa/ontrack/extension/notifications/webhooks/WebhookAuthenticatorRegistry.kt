package net.nemerosa.ontrack.extension.notifications.webhooks

interface WebhookAuthenticatorRegistry {

    fun findWebhookAuthenticator(type: String): WebhookAuthenticator<*>?

}