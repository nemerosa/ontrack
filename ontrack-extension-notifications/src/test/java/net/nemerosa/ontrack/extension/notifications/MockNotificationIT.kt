package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Notification integration test using a mock channel.
 */
@AsAdminTest
class MockNotificationIT : AbstractNotificationTestSupport() {

    @Test
    fun `Notification for a branch being created`() {
        project {
            // Subscription to the creation of branches for this project
            val target = uid("t")
            eventSubscriptionService.subscribe(
                name = uid("m-"),
                channel = mockNotificationChannel,
                channelConfig = MockNotificationChannelConfig(target),
                projectEntity = this,
                keywords = null,
                origin = "test",
                contentTemplate = null,
                EventFactory.NEW_BRANCH,
            )
            // Creating a branch
            branch("my-branch")
            // Checking we got a message on the mock channel
            assertNotNull(mockNotificationChannel.messages[target]) { messages ->
                assertEquals(1, messages.size)
                assertEquals("New branch my-branch for project $name.", messages.first())
            }
        }
    }

}