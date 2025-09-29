package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import net.nemerosa.ontrack.extension.notifications.webhooks.WebhookAuthenticator

abstract class AbstractWebhookAuthenticator<C> : WebhookAuthenticator<C> {

    protected fun merge(input: String, existing: String): String =
        input.ifBlank { existing }

}