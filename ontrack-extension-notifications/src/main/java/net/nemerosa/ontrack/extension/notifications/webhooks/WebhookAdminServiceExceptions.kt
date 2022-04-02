package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.exceptions.InputException

class WebhookAlreadyExistsException(name: String) : InputException(
    """The webhook with name [$name] already exists."""
)
