package net.nemerosa.ontrack.extension.notifications.mock

import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class MockNotificationChannel :
    AbstractNotificationChannel<MockNotificationChannelConfig>(MockNotificationChannelConfig::class) {

    /**
     * List of messages received, indexed by target.
     */
    val messages = mutableMapOf<String, MutableList<String>>()

    override fun publish(config: MockNotificationChannelConfig, event: Event): NotificationResult {
        messages.getOrPut(config.target) { mutableListOf() }.add(event.renderText())
        return NotificationResult.ok()
    }

    override val type: String = "mock"
    override val enabled: Boolean = true
}