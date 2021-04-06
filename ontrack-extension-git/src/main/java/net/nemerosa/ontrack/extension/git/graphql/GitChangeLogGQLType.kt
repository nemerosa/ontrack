package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

/**
 * GraphQL type for a change log
 * @see net.nemerosa.ontrack.extension.git.model.GitChangeLog
 */
@Component
class GitChangeLogGQLType(
    private val gitUICommitGQLType: GitUICommitGQLType,
    private val gitChangeLogIssuesGQLType: GitChangeLogIssuesGQLType,
    private val gitService: GitService,
) : GQLType {

    override fun getTypeName(): String = GIT_CHANGE_LOG

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
            .name(GIT_CHANGE_LOG)
            // Commits
            .field { f ->
                f.name("commits")
                    .description("List of commits in the change log")
                    .type(listType(gitUICommitGQLType.typeRef))
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()
                        gitService.getChangeLogCommits(gitChangeLog).commits
                    }
            }
            // Issues
            .field { f ->
                f.name("issues")
                    .description("List of issues in the change log")
                    .type(gitChangeLogIssuesGQLType.typeRef)
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()
                        gitService.getChangeLogIssues(gitChangeLog)
                    }
            }
            // TODO File changes
            // OK
            .build()
    }

    companion object {
        const val GIT_CHANGE_LOG = "GitChangeLog"
    }
}