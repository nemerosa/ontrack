package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.NotificationRecordsOutputsQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SubscribeToEntityEventsMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.ProjectEntity

/**
 * Interface for the management of notifications in Ontrack.
 */
class NotificationsMgt(connector: Connector) : Connected(connector) {

    /**
     * Subscribes for notifications.
     *
     * @param channel Channel to send the notifications to
     * @param channelConfig Configuration of the channel
     * @param keywords Space-separated list of keywords to filter the events
     * @param events Events to listen to
     * @param projectEntity Entity to listen to (null for global subscriptions)
     * @param contentTemplate Custom template for the event
     */
    fun subscribe(
        channel: String,
        channelConfig: Any,
        keywords: String?,
        events: List<String>,
        projectEntity: ProjectEntity?,
        contentTemplate: String? = null,
    ) {
        if (projectEntity != null) {
            graphqlConnector.mutate(
                SubscribeToEntityEventsMutation(
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

    /**
     * Access to the in-memory notification channel.
     */
    val inMemory: InMemoryMgt by lazy {
        InMemoryMgt(connector)
    }

    /**
     * Gets the last notification record outputs for a given channel
     */
    fun notificationRecordsOutputs(channel: String?): List<NotificationRecordOutput> {
        return graphqlConnector.query(
            NotificationRecordsOutputsQuery(Input.fromNullable(channel))
        )?.notificationRecords()?.pageItems()
            ?.map { it.result() }
            ?.map {
                NotificationRecordOutput(
                    type = it.type().name,
                    message = it.message(),
                    output = it.output(),
                )
            } ?: emptyList()
    }


}