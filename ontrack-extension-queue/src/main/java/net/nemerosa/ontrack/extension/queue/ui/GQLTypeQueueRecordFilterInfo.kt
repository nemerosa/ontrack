package net.nemerosa.ontrack.extension.queue.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeQueueRecordFilterInfo: GQLType {

    override fun getTypeName(): String = QueueRecordFilterInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(QueueRecordFilterInfo::class, cache)
}