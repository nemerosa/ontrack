package net.nemerosa.ontrack.extension.notifications.recording

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.notifications.channels.GQLTypeNotificationResult
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.dateField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeNotificationRecord(
    private val gqlTypeNotificationResult: GQLTypeNotificationResult,
) : GQLType {

    override fun getTypeName(): String = NotificationRecord::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Notification record")
            .stringField(NotificationRecord::id)
            .dateField(
                NotificationRecord::timestamp.name,
                getPropertyDescription(NotificationRecord::timestamp)
            )
            .stringField(NotificationRecord::channel)
            .field {
                it.name(NotificationRecord::channelConfig.name)
                    .description(getPropertyDescription(NotificationRecord::channelConfig))
                    .type(GQLScalarJSON.INSTANCE.toNotNull())
                    .dataFetcher { env ->
                        env.getSource<NotificationRecord>().channelConfig.asJson()
                    }
            }
            .field {
                it.name(NotificationRecord::event.name)
                    .description(getPropertyDescription(NotificationRecord::event))
                    .type(GQLScalarJSON.INSTANCE.toNotNull())
                    .dataFetcher { env ->
                        env.getSource<NotificationRecord>().event.asJson()
                    }
            }
            .field {
                it.name(NotificationRecord::result.name)
                    .description(getPropertyDescription(NotificationRecord::result))
                    .type(gqlTypeNotificationResult.typeRef.toNotNull())
            }
            .build()
}