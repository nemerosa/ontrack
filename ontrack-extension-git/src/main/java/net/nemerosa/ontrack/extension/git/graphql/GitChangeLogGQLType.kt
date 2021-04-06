package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.git.model.GitProjectNotConfiguredException
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * GraphQL type for a change log
 * @see net.nemerosa.ontrack.extension.git.model.GitChangeLog
 */
@Component
class GitChangeLogGQLType(
    private val gitUICommitGQLType: GitUICommitGQLType,
    private val gitChangeLogIssuesGQLType: GitChangeLogIssuesGQLType,
    private val issueChangeLogExportRequestGQLInputType: IssueChangeLogExportRequestGQLInputType,
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
            // Export of change log
            .field { f ->
                f.name("export")
                    .description("Export of the change log according to some specifications")
                    .type(GraphQLString)
                    .argument {
                        it.name("request")
                            .description("Export specifications")
                            .type(issueChangeLogExportRequestGQLInputType.typeRef)
                    }
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()
                        // Parses the request
                        val request = parseExportRequest(env)
                        // Build boundaries
                        request.from = gitChangeLog.from.build.id
                        request.to = gitChangeLog.to.build.id
                        // Gets the associated project
                        val project = gitChangeLog.project
                        // Gets the configuration for the project
                        val gitConfiguration = gitService.getProjectConfiguration(project)
                            ?: return@dataFetcher null
                        // Gets the issue service
                        val optConfiguredIssueService = gitConfiguration.configuredIssueService
                        if (!optConfiguredIssueService.isPresent) {
                            return@dataFetcher null
                        }
                        val configuredIssueService = optConfiguredIssueService.get()
                        // Gets the issue change log
                        val changeLogIssues = gitService.getChangeLogIssues(gitChangeLog)
                        // List of issues
                        val issues = changeLogIssues.list.map { it.issue }
                        // Exports the change log using the given format
                        val exportedChangeLogIssues = configuredIssueService.issueServiceExtension
                            .exportIssues(
                                configuredIssueService.issueServiceConfiguration,
                                issues,
                                request
                            )
                        // Returns the content
                        exportedChangeLogIssues.content
                    }
            }
            // OK
            .build()
    }

    private fun parseExportRequest(env: DataFetchingEnvironment): IssueChangeLogExportRequest {
        val requestArg: Any? = env.getArgument<Any>("request")
        return issueChangeLogExportRequestGQLInputType.convert(requestArg) ?: IssueChangeLogExportRequest()
    }

    companion object {
        const val GIT_CHANGE_LOG = "GitChangeLog"
    }
}