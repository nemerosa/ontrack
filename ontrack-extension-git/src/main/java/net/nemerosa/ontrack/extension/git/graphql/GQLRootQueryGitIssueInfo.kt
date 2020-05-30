package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.GitIssueSearchExtension
import net.nemerosa.ontrack.extension.git.model.OntrackGitIssueInfo
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitIssueInfo(
        private val ontrackGitIssueInfo: OntrackGitIssueInfoGQLType,
        private val searchService: SearchService,
        private val gitService: GitService
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitIssueInfo")
                    .description("Getting Ontrack information about a Git issue and a project.")
                    .argument {
                        it.name(ARG_ISSUE)
                                .description("Issue key")
                                .type(GraphQLNonNull(GraphQLString))
                    }
                    .type(ontrackGitIssueInfo.typeRef)
                    .dataFetcher { env -> getOntrackGitIssueInfo(env) }
                    .build()

    private fun getOntrackGitIssueInfo(env: DataFetchingEnvironment): OntrackGitIssueInfo? {
        val issue: String = env.getArgument(ARG_ISSUE)
        // Looking for the project based on the commit only
        val results = searchService.paginatedSearch(SearchRequest(
                token = issue,
                type = GitIssueSearchExtension.GIT_ISSUE_SEARCH_RESULT_TYPE
        ))
        // Getting the project
        val project: Project? = if (results.items.isEmpty() || results.items.size > 1) {
            null
        } else {
            val result = results.items.first()
            val data = result.data
            data?.get(GitIssueSearchExtension.GIT_ISSUE_SEARCH_RESULT_DATA_PROJECT) as? Project?
        }
        // Calling the Git service
        return project?.let { gitService.getIssueProjectInfo(it.id, issue) }
    }

    companion object {
        const val ARG_ISSUE = "issue"
    }
}