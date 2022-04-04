package net.nemerosa.ontrack.extension.notifications.webhooks

import org.springframework.stereotype.Component

@Component
class DefaultWebhookAuthenticatorRegistry(
    webhookAuthenticators: List<WebhookAuthenticator<*>>,
) : WebhookAuthenticatorRegistry {

    private val index = webhookAuthenticators.associateBy { it.type }

    override val authenticators: List<WebhookAuthenticator<*>> = index.values.sortedBy { it.type }

    override fun findWebhookAuthenticator(type: String): WebhookAuthenticator<*>? = index[type]

}