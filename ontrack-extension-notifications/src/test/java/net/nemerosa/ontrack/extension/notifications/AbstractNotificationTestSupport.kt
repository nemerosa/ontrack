package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

/**
 * Notification integration test using a mock channel.
 */
@TestPropertySource(
    properties = [
        "ontrack.extension.notifications.queue.async=false",
    ]
)
abstract class AbstractNotificationTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    protected lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    protected lateinit var eventFactory: EventFactory

}