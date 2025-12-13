package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.GroupMapping
import org.springframework.stereotype.Component

@Component
class GQLTypeGroupMapping : GQLType {
    override fun getTypeName(): String = GroupMapping::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(GroupMapping::class))
            .stringField(GroupMapping::idpGroup)
            .field(GroupMapping::group)
            .build()

}