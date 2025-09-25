package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.model.BranchInfos
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

/**
 * Collecting information about a set of branches.
 */
@Component
class GQLTypeBranchInfos : GQLType {

    override fun getTypeName(): String = BranchInfos::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Collecting information about a set of branches.")
            .stringField(BranchInfos::type)
            .listField(BranchInfos::branchInfoList)
            .build()


}