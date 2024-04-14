package net.nemerosa.ontrack.extension.notifications.channels

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordResult
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeNotificationRecordResult(
    private val gqlEnumNotificationResultType: GQLEnumNotificationResultType,
) : GQLType {

    override fun getTypeName(): String = NotificationRecordResult::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Result for a notification")
            .field {
                it.name(NotificationRecordResult::type.name)
                    .description(getPropertyDescription(NotificationResult<Any>::type))
                    .type(gqlEnumNotificationResultType.getTypeRef().toNotNull())
            }
            .stringField(NotificationRecordResult::message)
            .jsonField(NotificationRecordResult::output)
            .build()
}