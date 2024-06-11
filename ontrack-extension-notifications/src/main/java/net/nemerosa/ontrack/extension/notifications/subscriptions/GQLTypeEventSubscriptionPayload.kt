package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeEventSubscriptionPayload(
    private val notificationChannelRegistry: NotificationChannelRegistry,
) : GQLType {
    override fun getTypeName(): String = EventSubscriptionPayload::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description(getAPITypeName(EventSubscriptionPayload::class))
        .field {
            it.name("id")
                .description("Name of the subscription")
                .deprecate("Will be removed in V5. Use `name` instead.")
                .type(GraphQLString)
                .dataFetcher { env ->
                    env.getSource<EventSubscriptionPayload>().name
                }
        }
        .stringField(EventSubscriptionPayload::name)
        .stringField(EventSubscriptionPayload::channel)
        .field {
            it.name(EventSubscriptionPayload::channelConfig.name)
                .description(getPropertyDescription(EventSubscriptionPayload::channelConfig))
                .type(GQLScalarJSON.INSTANCE.toNotNull())
        }
        .field {
            it.name("channelConfigText")
                .description("Textual description of the channel configuration")
                .type(GraphQLString.toNotNull())
                .dataFetcher { env ->
                    val payload = env.getSource<EventSubscriptionPayload>()
                    notificationChannelRegistry.findChannel(payload.channel)?.run {
                        channelConfigText(this, payload.channelConfig)
                    }

                }
        }
        .field {
            it.name(EventSubscriptionPayload::events.name)
                .description(getPropertyDescription(EventSubscriptionPayload::events))
                .type(listType(GraphQLString))
        }
        .stringField(EventSubscriptionPayload::keywords)
        .booleanField(EventSubscriptionPayload::disabled)
        .stringField(EventSubscriptionPayload::contentTemplate)
        .build()

    private fun <C, R> channelConfigText(channel: NotificationChannel<C, R>, channelConfig: JsonNode): String {
        val config = channel.validate(channelConfig)
        return config.config?.run { channel.toText(this) } ?: ""
    }

}