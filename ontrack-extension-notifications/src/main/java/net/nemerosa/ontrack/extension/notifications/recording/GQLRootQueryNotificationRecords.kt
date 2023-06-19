package net.nemerosa.ontrack.extension.notifications.recording

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.notifications.channels.GQLEnumNotificationResultType
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryNotificationRecords(
    private val notificationRecordingService: NotificationRecordingService,
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeNotificationRecord: GQLTypeNotificationRecord,
    private val gqlEnumNotificationResultType: GQLEnumNotificationResultType,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, NotificationRecord>(
            cache = GQLTypeCache(),
            fieldName = "notificationRecords",
            fieldDescription = "Access to the notification recordings",
            itemType = gqlTypeNotificationRecord.typeName,
            arguments = listOf(
                GraphQLArgument.newArgument()
                    .name(ARG_FILTER_RESULT_TYPE)
                    .description("Filtering on the result type")
                    .type(gqlEnumNotificationResultType.getTypeRef())
                    .build()
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                val resultType = env.getArgument<String?>(ARG_FILTER_RESULT_TYPE)?.let {
                    NotificationResultType.valueOf(it)
                }
                val filter = NotificationRecordFilter(
                    offset = offset,
                    size = size,
                    resultType = resultType,
                )
                notificationRecordingService.filter(filter)
            }
        )

    companion object {
        private const val ARG_FILTER_RESULT_TYPE = "resultType"
    }
}