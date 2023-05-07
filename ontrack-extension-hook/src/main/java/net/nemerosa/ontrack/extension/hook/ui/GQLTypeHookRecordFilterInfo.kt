package net.nemerosa.ontrack.extension.hook.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeHookRecordFilterInfo : GQLType {

    override fun getTypeName(): String = HookRecordFilterInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectType(HookRecordFilterInfo::class, cache)
}