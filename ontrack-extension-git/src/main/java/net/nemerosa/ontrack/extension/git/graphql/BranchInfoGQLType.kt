package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.git.model.BranchInfo
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

/**
 * See [BranchInfo].
 */
@Component
class BranchInfoGQLType : GQLType {
    override fun getTypeName(): String =
            BranchInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Branch and associated information")
                    // Branch
                    .field {
                        it.name("branch")
                                .description("Associated branch")
                                .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
                    }
                    // First build
                    .field {
                        it.name("firstBuild")
                                .description("First build which contains the commit")
                                .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    }
                    // Promotions
                    .field {
                        it.name("promotions")
                                .description("First promotion for every promotion level")
                                .type(stdList(GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN)))
                    }
                    // OK
                    .build()

}