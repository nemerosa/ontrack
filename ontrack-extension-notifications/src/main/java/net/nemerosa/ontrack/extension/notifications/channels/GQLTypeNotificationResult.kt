package net.nemerosa.ontrack.extension.notifications.channels

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeNotificationResult(
    private val gqlEnumNotificationResultType: GQLEnumNotificationResultType,
) : GQLType {

    override fun getTypeName(): String = NotificationResult::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Result for a notification")
            .field {
                it.name(NotificationResult::type.name)
                    .description(getPropertyDescription(NotificationResult::type))
                    .type(gqlEnumNotificationResultType.getTypeRef().toNotNull())
            }
            .stringField(NotificationResult::id)
            .stringField(NotificationResult::message)
            .build()
}