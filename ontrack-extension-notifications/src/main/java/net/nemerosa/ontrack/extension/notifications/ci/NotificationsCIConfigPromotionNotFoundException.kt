package net.nemerosa.ontrack.extension.notifications.ci

import net.nemerosa.ontrack.model.exceptions.InputException

class NotificationsCIConfigPromotionNotFoundException(promotion: String) : InputException(
    """Promotion $promotion cannot be found."""
)
