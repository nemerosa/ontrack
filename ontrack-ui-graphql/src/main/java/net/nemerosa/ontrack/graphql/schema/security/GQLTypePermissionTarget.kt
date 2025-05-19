package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.PermissionTarget
import org.springframework.stereotype.Component

@Component
class GQLTypePermissionTarget : GQLType {

    override fun getTypeName(): String = PermissionTarget::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(PermissionTarget::class))
            .enumField(PermissionTarget::type)
            .intField(PermissionTarget::id)
            .stringField(PermissionTarget::name)
            .stringField(PermissionTarget::description)
            .build()
}