package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationSource
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationSourceDataType
import net.nemerosa.ontrack.extension.notifications.mock.OtherMockNotificationChannel
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.events.EventFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

/**
 * Notification integration test using a mock channel.
 */
@QueueNoAsync
abstract class AbstractNotificationTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var eventSubscriptionService: EventSubscriptionService

    @Autowired
    protected lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    protected lateinit var otherMockNotificationChannel: OtherMockNotificationChannel

    @Autowired
    protected lateinit var mockNotificationSource: MockNotificationSource

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var notificationsConfigProperties: NotificationsConfigProperties

    private var notificationsConfigPropertiesEnabled = false

    @BeforeEach
    fun before() {
        notificationsConfigPropertiesEnabled = notificationsConfigProperties.enabled
        notificationsConfigProperties.enabled = true
    }

    @AfterEach
    fun after() {
        notificationsConfigProperties.enabled = notificationsConfigPropertiesEnabled
    }

    protected fun mockSource(text: String = "test") = mockNotificationSource.createData(
        MockNotificationSourceDataType(
            text = text,
        )
    )

}