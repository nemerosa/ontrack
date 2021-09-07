package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.BranchInfos
import net.nemerosa.ontrack.extension.git.model.OntrackGitCommitInfo
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

/**
 * GraphQL type for [OntrackGitCommitInfo].
 */
@Component
class OntrackGitCommitInfoGQLType(
        private val gitUICommitGQLType: GitUICommitGQLType,
        private val branchInfosGQLType: BranchInfosGQLType
) : GQLType {
    override fun getTypeName(): String =
            OntrackGitCommitInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Information about a commit")
                    // UI commit
                    .field {
                        it.name("uiCommit")
                                .description("Information about the commit itself")
                                .type(gitUICommitGQLType.typeRef)
                    }
                    // Branch infos
                    .field {
                        it.name("branchInfosList")
                                .description("Associated branch info per types of branches")
                                .type(listType(branchInfosGQLType.typeRef))
                                .dataFetcher { environment ->
                                    val gitCommitInfo = environment.getSource<OntrackGitCommitInfo>()
                                    BranchInfos.toList(gitCommitInfo.branchInfos)
                                }
                    }
                    // OK
                    .build()
}