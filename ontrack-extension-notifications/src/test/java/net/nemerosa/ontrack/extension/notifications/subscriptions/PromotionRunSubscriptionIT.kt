package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PromotionRunSubscriptionIT : AbstractNotificationTestSupport() {

    @Test
    fun `Subscribing to a promotion run using the exact promotion level name`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel()
                    val silver = promotionLevel()
                    // Subscribing at branch level for the bronze promotion
                    val target = uid("t_")
                    eventSubscriptionService.subscribe(
                        name = uid("p"),
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(target),
                        projectEntity = this, // The branch
                        keywords = silver.name, // Promotion level name
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_PROMOTION_RUN
                    )
                    build {
                        // Bronze promotion --> no message
                        promote(bronze)
                        assertNull(mockNotificationChannel.messages[target], "No message")
                        // Silver promotion --> we get a message
                        promote(silver)
                        assertNotNull(mockNotificationChannel.messages[target], "Message received") { messages ->
                            assertEquals(1, messages.size, "One message received")
                            assertEquals(
                                "Build $name has been promoted to ${silver.name} for branch ${branch.name} in ${project.name}.",
                                messages.first()
                            )
                        }
                    }
                }
            }
        }
    }

}