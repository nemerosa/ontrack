package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.model.security.GlobalPermission
import org.springframework.stereotype.Component

@Component
class GQLTypeGlobalPermission : GQLType {

    override fun getTypeName(): String = GlobalPermission::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(GlobalPermission::class))
            .field(GlobalPermission::target)
            .field(GlobalPermission::role)
            .build()
}
