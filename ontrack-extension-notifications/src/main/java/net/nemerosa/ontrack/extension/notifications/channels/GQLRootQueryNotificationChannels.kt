package net.nemerosa.ontrack.extension.notifications.channels

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryNotificationChannels(
    private val gqlTypeNotificationChannel: GQLTypeNotificationChannel,
    private val notificationChannelRegistry: NotificationChannelRegistry,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("notificationChannels")
            .description("List of all notification channels")
            .type(listType(gqlTypeNotificationChannel.typeRef))
            .dataFetcher {
                notificationChannelRegistry.channels.sortedBy { it.type }
            }
            .build()
}