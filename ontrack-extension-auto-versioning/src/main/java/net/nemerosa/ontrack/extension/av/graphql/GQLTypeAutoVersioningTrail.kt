package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrail
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningTrail : GQLType {

    override fun getTypeName(): String = AutoVersioningTrail::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(AutoVersioningTrail::class))
            .listField(AutoVersioningTrail::potentialTargetBranches)
            .listField(AutoVersioningTrail::rejectedTargetBranches)
            .build()
}