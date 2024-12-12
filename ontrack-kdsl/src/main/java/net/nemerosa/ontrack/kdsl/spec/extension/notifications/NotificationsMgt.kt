package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.NotificationRecordsQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SubscribeToEntityEventsMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SubscriptionsByEntityQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.ProjectEntity

/**
 * Interface for the management of notifications in Ontrack.
 */
class NotificationsMgt(connector: Connector) : Connected(connector) {

    /**
     * Subscribes for notifications.
     *
     * @param name Subscription name (can be null for backward compatibility, but will be required in V5)
     * @param channel Channel to send the notifications to
     * @param channelConfig Configuration of the channel
     * @param keywords Space-separated list of keywords to filter the events
     * @param events Events to listen to
     * @param projectEntity Entity to listen to (null for global subscriptions)
     * @param contentTemplate Custom template for the event
     */
    fun subscribe(
        name: String? = null,
        channel: String,
        channelConfig: Any,
        keywords: String? = null,
        events: List<String>,
        projectEntity: ProjectEntity?,
        contentTemplate: String? = null,
    ) {
        if (projectEntity != null) {
            graphqlConnector.mutate(
                SubscribeToEntityEventsMutation(
                    Input.optional(name),
                    projectEntity.type,
                    projectEntity.id.toInt(),
                    channel,
                    channelConfig.asJson(),
                    Input.optional(keywords),
                    events,
                    Input.optional(contentTemplate),
                )
            ) {
                it?.subscribeToEvents()?.fragments()?.payloadUserErrors()?.convert()
            }
        } else {
            TODO("Global subscriptions not supported yet")
        }
    }

    fun subscriptions(projectEntity: ProjectEntity, offset: Int = 0, size: Int = 10): List<Subscription> =
        graphqlConnector.query(
            SubscriptionsByEntityQuery(
                projectEntity.type,
                projectEntity.id.toInt(),
                offset,
                size
            )
        )?.eventSubscriptions()?.pageItems()?.map {
            Subscription(
                name = it.name(),
                channel = it.channel(),
                channelConfig = it.channelConfig(),
                events = it.events(),
                keywords = it.keywords(),
                disabled = it.disabled() ?: false,
                contentTemplate = it.contentTemplate(),
            )
        } ?: emptyList()

    /**
     * Access to the in-memory notification channel.
     */
    val inMemory: InMemoryMgt by lazy {
        InMemoryMgt(connector)
    }

    /**
     * Gets the last notification record for a given channel
     */
    fun notificationRecords(channel: String?): List<NotificationRecord> {
        return graphqlConnector.query(
            NotificationRecordsQuery(Input.fromNullable(channel))
        )?.notificationRecords()?.pageItems()
            ?.map {
                NotificationRecord(
                    id = it.id(),
                    source = it.source()?.let { source ->
                        NotificationSourceData(
                            id = source.id(),
                            data = source.data(),
                        )
                    },
                    timestamp = it.timestamp(),
                    channel = it.channel(),
                    channelConfig = it.channelConfig(),
                    event = it.event(),
                    result = it.result().let { result ->
                        NotificationRecordResult(
                            type = result.type().name,
                            message = result.message(),
                            output = result.output(),
                        )
                    }
                )
            } ?: emptyList()
    }


}