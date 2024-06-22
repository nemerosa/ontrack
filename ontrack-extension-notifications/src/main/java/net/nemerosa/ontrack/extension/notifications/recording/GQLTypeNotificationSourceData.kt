package net.nemerosa.ontrack.extension.notifications.recording

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.notifications.model.NotificationSourceData
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeNotificationSourceData : GQLType {

    override fun getTypeName(): String = NotificationSourceData::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(NotificationSourceData::class, cache)

}