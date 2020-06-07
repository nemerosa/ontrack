package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.GitCommitSearchExtension
import net.nemerosa.ontrack.extension.git.model.OntrackGitCommitInfo
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitCommitInfo(
        private val ontrackGitCommitInfo: OntrackGitCommitInfoGQLType,
        private val searchService: SearchService,
        private val gitService: GitService
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitCommitInfo")
                    .description("Getting Ontrack information about a Git commit and a project.")
                    .argument {
                        it.name(ARG_COMMIT)
                                .description("Commit hash (full form) to look for.")
                                .type(GraphQLNonNull(GraphQLString))
                    }
                    .type(ontrackGitCommitInfo.typeRef)
                    .dataFetcher { env -> getOntrackGitCommitInfo(env) }
                    .build()

    private fun getOntrackGitCommitInfo(env: DataFetchingEnvironment): OntrackGitCommitInfo? {
        val commit: String = env.getArgument(ARG_COMMIT)
        // Looking for the project based on the commit only
        val results = searchService.paginatedSearch(SearchRequest(
                token = commit,
                type = GitCommitSearchExtension.GIT_COMMIT_SEARCH_RESULT_TYPE
        ))
        val project: Project? = if (results.items.isEmpty() || results.items.size > 1) {
            null
        } else {
            val result = results.items.first()
            val data = result.data
            data?.get(GitCommitSearchExtension.GIT_COMMIT_SEARCH_RESULT_DATA_PROJECT) as? Project?
        }
        // Calling the Git service
        return project?.let { gitService.getCommitProjectInfo(it.id, commit) }
    }

    companion object {
        const val ARG_COMMIT = "commit"
    }
}