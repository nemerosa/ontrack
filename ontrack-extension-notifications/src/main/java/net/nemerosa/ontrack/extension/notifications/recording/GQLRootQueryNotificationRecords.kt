package net.nemerosa.ontrack.extension.notifications.recording

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.notifications.channels.GQLEnumNotificationResultType
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
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
                    .name(ARG_FILTER_CHANNEL)
                    .description("Filtering on the channel")
                    .type(GraphQLString)
                    .build(),
                GraphQLArgument.newArgument()
                    .name(ARG_FILTER_RESULT_TYPE)
                    .description("Filtering on the result type")
                    .type(gqlEnumNotificationResultType.getTypeRef())
                    .build(),
                stringArgument(ARG_FILTER_SOURCE_ID, "Filtering on the source type"),
                GraphQLArgument.newArgument()
                    .name(ARG_FILTER_SOURCE_DATA)
                    .description("Filtering on the source data")
                    .type(GQLScalarJSON.INSTANCE)
                    .build(),
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                val resultType = env.getArgument<String?>(ARG_FILTER_RESULT_TYPE)?.let {
                    NotificationResultType.valueOf(it)
                }
                val channel: String? = env.getArgument(ARG_FILTER_CHANNEL)
                val sourceId: String? = env.getArgument(ARG_FILTER_SOURCE_ID)
                val sourceData: JsonNode? = env.getArgument(ARG_FILTER_SOURCE_DATA)
                val filter = NotificationRecordFilter(
                    offset = offset,
                    size = size,
                    channel = channel,
                    resultType = resultType,
                    sourceId = sourceId,
                    sourceData = sourceData,
                )
                notificationRecordingService.filter(filter)
            }
        )

    companion object {
        private const val ARG_FILTER_CHANNEL = "channel"
        private const val ARG_FILTER_RESULT_TYPE = "resultType"
        private const val ARG_FILTER_SOURCE_ID = "sourceId"
        private const val ARG_FILTER_SOURCE_DATA = "sourceData"
    }
}