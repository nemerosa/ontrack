package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.model.BranchInfo
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

/**
 * Collecting information about a set of branches.
 */
@Component
class GQLTypeBranchInfo : GQLType {

    override fun getTypeName(): String = BranchInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about a branch")
            .field(BranchInfo::branch)
            .field(BranchInfo::firstBuild)
            .listField(BranchInfo::promotions)
            .build()


}