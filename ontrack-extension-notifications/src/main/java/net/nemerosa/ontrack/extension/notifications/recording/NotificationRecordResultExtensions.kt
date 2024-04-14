package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson

fun NotificationResult<*>.toNotificationRecordResult() = NotificationRecordResult(
    type = type,
    message = message,
    output = output?.asJson(),
)
