package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class EventSubscriptionServiceIT : AbstractNotificationTestSupport() {

    @Test
    fun `Getting the subscriptions for an event`() {
        val targetBranch = uid("tb")
        val targetProject = uid("tp")
        project {
            branch {
                // Subscription at branch level
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetBranch),
                    projectEntity = this, // Branch
                    EventFactory.NEW_PROMOTION_RUN
                )
                // Subscription at project level
                eventSubscriptionService.subscribe(
                    channel = mockNotificationChannel,
                    channelConfig = MockNotificationChannelConfig(targetProject),
                    projectEntity = project,
                    EventFactory.NEW_PROMOTION_RUN
                )
                // Gettings the subscriptions for this project & the new promotion run event
                // (without actually testing the notification)
                val build = Build.of(this, NameDescription.nd("1", ""), Signature.of("test")).withId(ID.of(10))
                val promotionLevel = PromotionLevel.of(this, NameDescription.nd("PL", "")).withId(ID.of(100))
                val subscriptions = mutableListOf<EventSubscription>()
                eventSubscriptionService.forEveryMatchingSubscription(
                    eventFactory.newPromotionRun(
                        PromotionRun.of(
                            build,
                            promotionLevel,
                            Signature.of("test"),
                            null
                        ).withId(ID.of(1000))
                    )
                ) { subscriptions += it }
                // Checks the subscriptions
                // We expect one for the branch and one for the project
                // Sorting per target
                subscriptions.sortBy { it.channels.first().channelConfig["target"].asText() }
                assertEquals(2, subscriptions.size)
                // Branch first (tb)
                subscriptions[0].let { subscription ->
                    assertEquals(this, subscription.projectEntity)
                    assertEquals(setOf(
                        EventSubscriptionChannel(
                            channel = "mock",
                            channelConfig = mapOf("target" to targetBranch).asJson()
                        )
                    ), subscription.channels)
                    assertEquals(setOf("new_promotion_run"), subscription.events)
                }
                // Project second (tp)
                subscriptions[1].let { subscription ->
                    assertEquals(project, subscription.projectEntity)
                    assertEquals(setOf(
                        EventSubscriptionChannel(
                            channel = "mock",
                            channelConfig = mapOf("target" to targetProject).asJson()
                        )
                    ), subscription.channels)
                    assertEquals(setOf("new_promotion_run"), subscription.events)
                }
            }
        }
    }

}