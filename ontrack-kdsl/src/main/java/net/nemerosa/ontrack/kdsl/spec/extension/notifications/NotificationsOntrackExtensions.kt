package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of notifications in Ontrack.
 */
val Ontrack.notifications: NotificationsMgt get() = NotificationsMgt(connector)
