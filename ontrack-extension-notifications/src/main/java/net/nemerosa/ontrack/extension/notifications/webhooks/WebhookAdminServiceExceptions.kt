package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException

class WebhookAlreadyExistsException(name: String) : InputException(
    """The webhook with name [$name] already exists."""
)

class WebhookNotFoundException(name: String) : NotFoundException(
    """The webhook with name [$name] does not exist."""
)
