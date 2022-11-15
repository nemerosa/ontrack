package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.User
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DefaultEventSubscriptionServiceTest {

    @Test
    fun `Backward compatibility with origin`() {
        val json = mapOf(
            "signature" to mapOf(
                "time" to "2022-11-09T16:33:00",
                "user" to mapOf(
                    "name" to "test"
                )
            ),
            "channel" to "slack",
            "channelConfig" to mapOf(
                "channel" to "#my-channel",
            ),
            "events" to listOf("some-event"),
            "keywords" to "test",
            "disabled" to false,
        ).asJson()
        val parsed = json.parse<DefaultEventSubscriptionService.SignedSubscriptionRecord>()
        assertEquals(
            DefaultEventSubscriptionService.SignedSubscriptionRecord(
                signature = Signature(
                    time = Time.fromStorage("2022-11-09T16:33:00")!!,
                    user = User("test")
                ),
                channel = "slack",
                channelConfig = mapOf(
                    "channel" to "#my-channel",
                ).asJson(),
                events = setOf("some-event"),
                keywords = "test",
                disabled = false,
                origin = "unknown",
            ),
            parsed
        )
    }

}