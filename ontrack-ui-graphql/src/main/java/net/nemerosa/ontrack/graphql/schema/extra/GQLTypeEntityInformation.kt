package net.nemerosa.ontrack.graphql.schema.extra

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeEntityInformation : GQLType {

    override fun getTypeName(): String = EntityInformation::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(EntityInformation::class))
            .jsonField(EntityInformation::data)
            .stringField(EntityInformation::type)
            .stringField(EntityInformation::title)
            .build()
}