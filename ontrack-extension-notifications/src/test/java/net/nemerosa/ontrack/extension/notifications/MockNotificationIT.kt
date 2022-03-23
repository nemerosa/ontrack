package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Notification integration test using a mock channel.
 */
class MockNotificationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    private lateinit var mockNotificationChannel: MockNotificationChannel

    @Test
    fun `Notification for a branch being created`() {
        project {
            // Subscription to the creation of branches for this project
            val target = uid("t")
            eventSubscriptionService.subscribe(
                mockNotificationChannel,
                MockNotificationChannelConfig(target),
                this,
                EventFactory.NEW_BRANCH,
            )
            // Creating a branch
            branch("my-branch")
            // TODO Checking we got a message on the mock channel
        }
    }

}