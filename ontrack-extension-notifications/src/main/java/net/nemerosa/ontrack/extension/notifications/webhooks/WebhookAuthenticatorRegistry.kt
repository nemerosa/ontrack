package net.nemerosa.ontrack.extension.notifications.webhooks

interface WebhookAuthenticatorRegistry {

    /**
     * List of available authenticators
     */
    val authenticators: List<WebhookAuthenticator<*>>

    fun findWebhookAuthenticator(type: String): WebhookAuthenticator<*>?

}