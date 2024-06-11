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
        val name = uid("g")
        casc("""
            ontrack:
                extensions:
                    notifications:
                        global-subscriptions:
                            - name: $name
                              events:
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
                name,
                subscription.name
            )
            assertEquals(
                setOf("new_promotion_run"),
                subscription.events
            )
            assertEquals(
                "GOLD main",
                subscription.keywords
            )
            assertEquals(
                "mock",
                subscription.channel
            )
            assertEquals(
                mapOf("target" to "#$target").asJson(),
                subscription.channelConfig
            )
            assertEquals(
                false,
                subscription.disabled
            )
        }
    }

    @Test
    fun `Creates a global subscription as code with a content template`() {
        val target = uid("t")
        val name = uid("g")
        casc("""
            ontrack:
                extensions:
                    notifications:
                        global-subscriptions:
                            - name: $name
                              events:
                                - new_promotion_run
                              keywords: "GOLD main"
                              channel: mock
                              channel-config:
                                target: "#$target"
                              contentTemplate: |
                                This is a fairly simple template
                                for a ${'$'}{branch} name and
                                a ${'$'}{changelog?format=html}.
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
                subscription.events
            )
            assertEquals(
                "GOLD main",
                subscription.keywords
            )
            assertEquals(
                "mock",
                subscription.channel
            )
            assertEquals(
                mapOf("target" to "#$target").asJson(),
                subscription.channelConfig
            )
            assertEquals(
                false,
                subscription.disabled
            )
            assertEquals(
                """
                    This is a fairly simple template
                    for a ${'$'}{branch} name and
                    a ${'$'}{changelog?format=html}.
                """.trimIndent(),
                subscription.contentTemplate
            )
        }
    }

}