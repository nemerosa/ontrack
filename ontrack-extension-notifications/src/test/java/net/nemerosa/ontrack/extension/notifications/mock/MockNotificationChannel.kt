package net.nemerosa.ontrack.extension.notifications.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import org.springframework.stereotype.Component

@Component
class MockNotificationChannel(
    private val eventTemplatingService: EventTemplatingService,
) :
    AbstractNotificationChannel<MockNotificationChannelConfig, MockNotificationChannelOutput>(MockNotificationChannelConfig::class) {

    /**
     * List of messages received, indexed by target.
     */
    val messages = mutableMapOf<String, MutableList<String>>()

    override fun publish(
        config: MockNotificationChannelConfig,
        event: Event,
        template: String?,
        outputProgressCallback: (current: MockNotificationChannelOutput) -> MockNotificationChannelOutput
    ): NotificationResult<MockNotificationChannelOutput> {
        val text = eventTemplatingService.renderEvent(
            event,
            template,
            PlainEventRenderer.INSTANCE,
        )
        messages.getOrPut(config.target) { mutableListOf() }.add(text)
        return NotificationResult.ok(
            output = MockNotificationChannelOutput(text = text)
        )
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(MockNotificationChannelConfig::target.name to text).asJson()

    override fun toText(config: MockNotificationChannelConfig): String = config.target

    @Deprecated("Will be removed in V5. Only Next UI is used.")
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