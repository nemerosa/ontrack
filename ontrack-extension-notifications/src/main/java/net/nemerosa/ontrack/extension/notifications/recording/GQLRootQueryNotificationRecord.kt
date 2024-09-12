package net.nemerosa.ontrack.extension.notifications.recording

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryNotificationRecord(
    private val notificationRecordingService: NotificationRecordingService,
    private val gqlTypeNotificationRecord: GQLTypeNotificationRecord,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("notificationRecord")
            .description("Getting a notification record using its ID")
            .type(gqlTypeNotificationRecord.typeRef)
            .argument(stringArgument(ARG_ID, "Record ID", nullable = false))
            .dataFetcher { env ->
                val id: String = env.getArgument(ARG_ID)
                notificationRecordingService.findRecordById(id)
            }
            .build()

    companion object {
        private const val ARG_ID = "id"
    }
}