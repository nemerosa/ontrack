package net.nemerosa.ontrack.extension.hook.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.nameValuesFromMapField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeHookRequest : GQLType {

    override fun getTypeName(): String = HookRequest::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(HookRequest::class))
            .stringField(HookRequest::body)
            .nameValuesFromMapField(HookRequest::parameters)
            .build()
}