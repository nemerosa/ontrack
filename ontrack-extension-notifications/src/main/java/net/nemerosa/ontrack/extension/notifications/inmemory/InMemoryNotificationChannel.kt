package net.nemerosa.ontrack.extension.notifications.inmemory

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.textField
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Storing the messages in memory.
 *
 * For test only, should not be used in production.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.in-memory",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class InMemoryNotificationChannel(
    private val eventTemplatingService: EventTemplatingService,
) :
    AbstractNotificationChannel<InMemoryNotificationChannelConfig>(InMemoryNotificationChannelConfig::class) {

    private val messages = mutableMapOf<String, MutableList<String>>()

    internal fun getMessages(group: String) = messages[group] ?: emptyList()

    override fun publish(
        config: InMemoryNotificationChannelConfig,
        event: Event,
        template: String?
    ): NotificationResult {
        val text = eventTemplatingService.renderEvent(
            event,
            template,
            PlainEventRenderer()
        )
        messages.getOrPut(config.group) { mutableListOf() }.add(text)
        return NotificationResult.ok()
    }

    override fun toSearchCriteria(text: String): JsonNode = InMemoryNotificationChannelConfig(text).asJson()

    override fun toText(config: InMemoryNotificationChannelConfig): String = config.group

    override fun getForm(c: InMemoryNotificationChannelConfig?): Form = Form.create()
        .textField(InMemoryNotificationChannelConfig::group, c?.group)

    fun clear(): Ack {
        messages.clear()
        return Ack.OK
    }

    override val type: String = "in-memory"

    override val enabled: Boolean = true
}