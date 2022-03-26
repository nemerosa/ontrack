package net.nemerosa.ontrack.extension.notifications.channels

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class NotificationChannelNotFoundException(type: String) : NotFoundException(
    """Notification channel $type cannot be found."""
)