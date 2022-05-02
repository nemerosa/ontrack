package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GlobalSubscriptionsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var eventSubscriptionService: EventSubscriptionService

    @Test
    fun `Creates a global subscription as code`() {
        val target = uid("t")
        casc("""
            ontrack:
                extensions:
                    notifications:
                        global-subscriptions:
                            - events:
                                - new_promotion_run
                              keywords: "GOLD main"
                              channel: mock
                              channel-config:
                                target: "#$target"
        """.trimIndent())
        // Check we can find this global subscriptions
        asAdmin {
            val subscriptions = eventSubscriptionService.filterSubscriptions(
                EventSubscriptionFilter(
                    channel = "mock",
                    channelConfig = "#$target"
                )
            )
            assertEquals(1, subscriptions.pageItems.size)
            val subscription = subscriptions.pageItems.first()
            assertEquals(
                setOf("new_promotion_run"),
                subscription.data.events
            )
            assertEquals(
                "GOLD main",
                subscription.data.keywords
            )
            assertEquals(
                "mock",
                subscription.data.channel
            )
            assertEquals(
                mapOf("target" to "#$target").asJson(),
                subscription.data.channelConfig
            )
            assertEquals(
                false,
                subscription.data.disabled
            )
        }
    }

}