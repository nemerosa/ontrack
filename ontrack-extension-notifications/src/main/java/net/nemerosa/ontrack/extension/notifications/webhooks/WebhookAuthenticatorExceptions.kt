package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.common.BaseException

class WebhookAuthenticatorNotFoundException(type: String) : BaseException(
    """Webhook authenticator with type ="$type is not supported."""
)
