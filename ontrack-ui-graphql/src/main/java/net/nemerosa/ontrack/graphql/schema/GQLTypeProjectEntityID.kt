package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.idFieldForInt
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectEntityID : GQLType {

    override fun getTypeName(): String = ProjectEntityID::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .enumField(ProjectEntityID::type)
            .idFieldForInt(ProjectEntityID::id)
            .build()
}