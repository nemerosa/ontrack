package net.nemerosa.ontrack.extension.notifications.channels

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeNotificationChannel : GQLType {

    override fun getTypeName(): String = NotificationChannel::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Notification channel")
            .stringField(NotificationChannel<Any, Any>::type)
            .booleanField(NotificationChannel<Any, Any>::enabled)
            .build()
}