package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.BranchInfos
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

/**
 * See [BranchInfos].
 */
@Component
class BranchInfosGQLType(
        private val branchInfoGQLType: BranchInfoGQLType
) : GQLType {
    override fun getTypeName(): String =
            BranchInfos::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Type of branch associated with information about the corresponding branches.")
                    // Type of branch
                    .field {
                        it.name("type")
                                .description("Type of branch")
                                .type(GraphQLString)
                    }
                    // List of branch
                    .field {
                        it.name("branchInfoList")
                                .description("List of branches and their associated information")
                                .type(stdList(branchInfoGQLType.typeRef))
                    }
                    // OK
                    .build()

}