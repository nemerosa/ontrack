package net.nemerosa.ontrack.extension.notifications.recording

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryNotificationRecords(
    private val notificationRecordingService: NotificationRecordingService,
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeNotificationRecord: GQLTypeNotificationRecord,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any, NotificationRecord>(
            cache = GQLTypeCache(),
            fieldName = "notificationRecords",
            fieldDescription = "Access to the notification recordings",
            itemType = gqlTypeNotificationRecord,
            itemPaginatedListProvider = { _, _, offset, size ->
                val filter = NotificationRecordFilter(
                    offset = offset,
                    size = size,
                )
                notificationRecordingService.filter(filter)
            }
        )
}