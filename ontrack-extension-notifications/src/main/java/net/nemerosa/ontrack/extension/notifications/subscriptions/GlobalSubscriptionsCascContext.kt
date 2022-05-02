package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.notifications.casc.NotificationsSubCascContext
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

@Component
class GlobalSubscriptionsCascContext(
    private val eventSubscriptionService: EventSubscriptionService,
) : AbstractCascContext(), NotificationsSubCascContext {

    override val field: String = "global-subscriptions"

    override val type: CascType = cascArray(
        "List of global subscriptions",
        cascObject(
            "Global subscription",
            cascField(GlobalSubscriptionsCascContextData::events, type = cascArray("List of event types", cascString)),
            cascField(GlobalSubscriptionsCascContextData::keywords),
            cascField(GlobalSubscriptionsCascContextData::channel),
            cascField(GlobalSubscriptionsCascContextData::channelConfig),
            cascField(GlobalSubscriptionsCascContextData::disabled),
        )
    )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parse<GlobalSubscriptionsCascContextData>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${GlobalSubscriptionsCascContextData::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }
        items.forEach { subscription ->
            eventSubscriptionService.subscribe(
                EventSubscription(
                    projectEntity = null,
                    events = subscription.events.toSet(),
                    keywords = subscription.keywords,
                    channel = subscription.channel,
                    channelConfig = subscription.channelConfig,
                    disabled = subscription.disabled ?: false,
                )
            )
        }
    }

    override fun render(): JsonNode =
        eventSubscriptionService.filterSubscriptions(
            EventSubscriptionFilter(
                size = 1000,
            )
        ).pageItems.map {
            GlobalSubscriptionsCascContextData(
                events = it.data.events.toList(),
                keywords = it.data.keywords,
                channel = it.data.channel,
                channelConfig = it.data.channelConfig,
                disabled = it.data.disabled,
            )
        }.asJson()

    data class GlobalSubscriptionsCascContextData(
        @APIDescription("List of events to listen to")
        val events: List<String>,
        @APIDescription("Keywords to filter the events")
        val keywords: String?,
        @APIDescription("Channel to send notifications to")
        val channel: String,
        @APIDescription("Configuration of the channel")
        @get:JsonProperty("channel-config")
        val channelConfig: JsonNode,
        @APIDescription("Is this channel disabled?")
        val disabled: Boolean? = null,
    )
}