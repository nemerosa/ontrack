package net.nemerosa.ontrack.extension.slack.service

import net.nemerosa.ontrack.extension.slack.notifications.SlackNotificationChannelConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SlackNotificationChannelConfigTest {

    @Test
    fun `Backward compatible parsing`() {
        assertEquals(
            SlackNotificationChannelConfig(
                channel = "#my-channel",
                type = SlackNotificationType.INFO,
            ),
            mapOf(
                "channel" to "#my-channel",
            ).asJson().parse()
        )
    }

    @Test
    fun `Config parsing`() {
        assertEquals(
            SlackNotificationChannelConfig(
                channel = "#my-channel",
                type = SlackNotificationType.SUCCESS,
            ),
            mapOf(
                "channel" to "#my-channel",
                "type" to "SUCCESS",
            ).asJson().parse()
        )
    }

}