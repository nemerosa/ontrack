package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

// @Component
class GQLTypeQueuePayload : GQLType {

    override fun getTypeName(): String = QueuePayload::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(QueuePayload::class, cache)

}