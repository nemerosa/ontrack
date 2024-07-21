package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.tracking.RejectedBranch
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeRejectedBranch : GQLType {

    override fun getTypeName(): String = RejectedBranch::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(RejectedBranch::class))
            .field(RejectedBranch::branch)
            .stringField(RejectedBranch::reason)
            .build()
}