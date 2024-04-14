package net.nemerosa.ontrack.kdsl.spec.extension.notifications.mail

import net.nemerosa.ontrack.kdsl.spec.extension.notifications.NotificationsMgt

val NotificationsMgt.mail get() = MailMgt(connector)
