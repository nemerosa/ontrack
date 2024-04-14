package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NotificationRecordTest {

    @Test
    fun `Reading JSON with result ID and without result output for backward compatibility`() {
        val stored = mapOf(
            "id" to "record-id",
            "timestamp" to "2024-04-12T06:58:59",
            "channel" to "mock",
            "channelConfig" to mapOf(
                "target" to "#target"
            ),
            "event" to mapOf(
                "id" to "some-event"
            ),
            "result" to mapOf(
                "type" to "OK",
                "message" to "some-message",
                "id" to "obsolete-id",
            )
        ).asJson()
        val record: NotificationRecord = stored.parse()
        assertEquals(
            null,
            record.result.output,
            "Legacy record result output set to null",
        )
    }

}