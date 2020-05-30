package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.model.OntrackGitIssueInfo
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitIssueInfo(
        private val ontrackGitIssueInfo: OntrackGitIssueInfoGQLType,
        private val structureService: StructureService,
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
                    .argument {
                        it.name(ARG_PROJECT)
                                .description("Name of the project where to restrict the search " +
                                        "(optional, but giving this information will improve the performances")
                                .type(GraphQLString)
                    }
                    .type(ontrackGitIssueInfo.typeRef)
                    .dataFetcher { env -> getOntrackGitIssueInfo(env) }
                    .build()

    private fun getOntrackGitIssueInfo(env: DataFetchingEnvironment): OntrackGitIssueInfo? {
        val issue: String = env.getArgument(ARG_ISSUE)
        val projectName: String? = env.getArgument(ARG_PROJECT)
        // Getting the project
        val project: Project = if (projectName != null) {
            structureService.findProjectByNameIfAuthorized(projectName)
                    ?: throw ProjectNotFoundException(projectName)
        }
        // If project is not given as a hint, we need to identify the project first
        else {
            TODO("Getting the project from a Git commit")
        }
        // Calling the Git service
        return gitService.getIssueProjectInfo(project.id, issue)
    }

    companion object {
        const val ARG_ISSUE = "issue"
        const val ARG_PROJECT = "project"
    }
}