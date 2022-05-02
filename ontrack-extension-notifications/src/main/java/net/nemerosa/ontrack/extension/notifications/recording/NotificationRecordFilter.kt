package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType

data class NotificationRecordFilter(
    val offset: Int = 0,
    val size: Int = 10,
    val resultType: NotificationResultType? = null,
)