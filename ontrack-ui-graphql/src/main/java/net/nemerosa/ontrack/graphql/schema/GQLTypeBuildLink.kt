package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.BuildLink
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildLink : GQLType {

    override fun getTypeName(): String = BuildLink::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Qualified link to a build")
            .field(BuildLink::build, GQLTypeBuild.BUILD)
            .stringField(BuildLink::qualifier)
            .build()

}