package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.notifications.casc.NotificationsSubCascContext
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
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
            cascField(SubscriptionsCascContextData::events, type = cascArray("List of event types", cascString)),
            cascField(SubscriptionsCascContextData::keywords),
            cascField(SubscriptionsCascContextData::channel),
            cascField(SubscriptionsCascContextData::channelConfig),
            cascField(SubscriptionsCascContextData::disabled),
        )
    )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parse<SubscriptionsCascContextData>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${SubscriptionsCascContextData::class.qualifiedName}: ${path(paths + index.toString())}",
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
                    origin = EventSubscriptionOrigins.API,
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
            SubscriptionsCascContextData(
                events = it.data.events.toList(),
                keywords = it.data.keywords,
                channel = it.data.channel,
                channelConfig = it.data.channelConfig,
                disabled = it.data.disabled,
                origin = it.data.origin,
            )
        }.asJson()

}