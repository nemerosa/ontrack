package net.nemerosa.ontrack.extension.notifications.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
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

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(MockNotificationChannelConfig::target.name to text).asJson()

    override fun toText(config: MockNotificationChannelConfig): String = config.target

    override fun getForm(c: MockNotificationChannelConfig?): Form = Form.create()
        .with(
            Text.of(MockNotificationChannelConfig::target.name)
                .label("Target")
                .help("Test target")
                .value(c?.target)
        )

    override val type: String = "mock"
    override val enabled: Boolean = true
}