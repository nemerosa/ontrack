package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.mock.OtherMockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.events.EventFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

/**
 * Notification integration test using a mock channel.
 */
@TestPropertySource(
    properties = [
        "ontrack.config.extension.notifications.dispatching.queue.async=false",
        "ontrack.config.extension.notifications.processing.queue.async=false",
    ]
)
abstract class AbstractNotificationTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    protected lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    protected lateinit var otherMockNotificationChannel: OtherMockNotificationChannel

    @Autowired
    protected lateinit var eventFactory: EventFactory

}