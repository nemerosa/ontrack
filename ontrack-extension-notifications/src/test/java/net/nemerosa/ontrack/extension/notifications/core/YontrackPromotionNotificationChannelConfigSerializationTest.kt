package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.junit.jupiter.api.Test
import java.time.Duration

class YontrackPromotionNotificationChannelConfigSerializationTest {

    @Test
    fun `Serialization of the duration using shorthand`() {
        val config = YontrackPromotionNotificationChannelConfig(
            promotion = "GOLD",
            waitForPromotionTimeout = Duration.ofMinutes(30)
        )
        val json = config.asJson().format()
        // Check that the output contains the shorthand duration
        // We use contains because the exact JSON might have other fields and formatting
        assert(json.contains("\"waitForPromotionTimeout\":\"30m\""))
    }

    @Test
    fun `Serialization of the duration using ISO if not matching shorthand`() {
        // 30 minutes and 5 seconds doesn't match any shorthand in Duration.format()
        val config = YontrackPromotionNotificationChannelConfig(
            promotion = "GOLD",
            waitForPromotionTimeout = Duration.ofMinutes(30).plusSeconds(5)
        )
        val json = config.asJson().format()
        assert(json.contains("\"waitForPromotionTimeout\":\"PT30M5S\""))
    }
}
