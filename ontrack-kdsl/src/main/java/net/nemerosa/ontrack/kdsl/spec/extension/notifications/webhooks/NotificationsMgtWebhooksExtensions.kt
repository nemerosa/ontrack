package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import net.nemerosa.ontrack.kdsl.spec.extension.notifications.NotificationsMgt

val NotificationsMgt.webhooks get() = WebhooksMgt(connector)
