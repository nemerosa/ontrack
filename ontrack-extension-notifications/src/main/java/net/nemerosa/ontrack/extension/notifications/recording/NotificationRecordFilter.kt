package net.nemerosa.ontrack.extension.notifications.recording

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType

data class NotificationRecordFilter(
    val offset: Int = 0,
    val size: Int = 10,
    val channel: String? = null,
    val resultType: NotificationResultType? = null,
    val sourceId: String? = null,
    val sourceData: JsonNode? = null,
)