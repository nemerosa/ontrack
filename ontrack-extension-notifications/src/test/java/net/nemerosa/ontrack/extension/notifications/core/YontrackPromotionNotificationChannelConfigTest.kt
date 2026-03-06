package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class YontrackPromotionNotificationChannelConfigTest {

    @Test
    fun `Parsing of the duration using shorthand`() {
        val config = mapOf(
            "promotion" to "GOLD",
            "waitForPromotionTimeout" to "5s",
        ).asJson().parse<YontrackPromotionNotificationChannelConfig>()

        assertEquals(
            Duration.ofSeconds(5),
            config.waitForPromotionTimeout
        )
    }

    @Test
    fun `Parsing of the duration using ISO`() {
        val config = mapOf(
            "promotion" to "GOLD",
            "waitForPromotionTimeout" to "PT30M",
        ).asJson().parse<YontrackPromotionNotificationChannelConfig>()

        assertEquals(
            Duration.ofMinutes(30),
            config.waitForPromotionTimeout
        )
    }

    @Test
    fun `Parsing of the duration using numeric seconds`() {
        val config = mapOf(
            "promotion" to "GOLD",
            "waitForPromotionTimeout" to "1800",
        ).asJson().parse<YontrackPromotionNotificationChannelConfig>()

        assertEquals(
            Duration.ofMinutes(30),
            config.waitForPromotionTimeout
        )
    }

}