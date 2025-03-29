package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.LinkChangeService
import org.springframework.stereotype.Component

/**
 * GraphQL type for a change log
 * @see net.nemerosa.ontrack.extension.git.model.GitChangeLog
 */
@Component
class GQLTypeGitChangeLog(
    private val gitUICommitGQLType: GQLTypeGitUICommit,
    private val gitChangeLogIssuesGQLType: GitChangeLogIssuesGQLType,
    private val gqlTypeGitChangeLogFiles: GQLTypeGitChangeLogFiles,
    private val issueChangeLogExportRequestGQLInputType: IssueChangeLogExportRequestGQLInputType,
    private val gitService: GitService,
    private val recursiveChangeLogService: RecursiveChangeLogService,
    private val gitChangeLogCache: GitChangeLogCache,
    private val gqlTypeLinkChange: GQLTypeLinkChange,
    private val linkChangeService: LinkChangeService,
) : GQLType {

    override fun getTypeName(): String = GIT_CHANGE_LOG

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
            .name(GIT_CHANGE_LOG)
            // UUID
            .field {
                it.name("uuid")
                    .description("UUID of the change log.")
                    .type(GraphQLNonNull(GraphQLString))
            }
            // Project
            .field {
                it.name("project")
                    .description("Project linked to the change log")
                    .type(GraphQLTypeReference(GQLTypeProject.PROJECT).toNotNull())
                    .dataFetcher { env ->
                        env.getSource<GitChangeLog>()!!.from.build.project
                    }
            }
            // Sync error
            .booleanField(GitChangeLog::syncError, "If an error has occured during the synchronization")
            // Build from & to
            .field {
                it.name("buildFrom")
                    .description("From build")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD).toNotNull())
                    .dataFetcher { env ->
                        env.getSource<GitChangeLog>()!!.from.build
                    }
            }
            .field {
                it.name("buildTo")
                    .description("To build")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD).toNotNull())
                    .dataFetcher { env ->
                        env.getSource<GitChangeLog>()!!.to.build
                    }
            }
            // Link to a diff
            .field {
                it.name("diffLink")
                    .description("Link to a diff between the two builds")
                    .type(GraphQLString)
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitService.getDiffLink(gitChangeLog)
                    }
            }
            // List of all dependency changes
            .field {
                it.name("linkChanges")
                    .description("All dependency changes")
                    .type(listType(gqlTypeLinkChange.typeRef))
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        linkChangeService.linkChanges(
                            gitChangeLog.from.build,
                            gitChangeLog.to.build,
                        )
                    }
            }
            // Commits plot
            .field {
                it.name("commitsPlot")
                    .description("List of commits as a plot")
                    .type(GQLScalarJSON.INSTANCE)
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitChangeLog.loadCommits {
                            gitService.getChangeLogCommits(gitChangeLog)
                        }.log.plot.asJson()
                    }
            }
            // Commits
            .field { f ->
                f.name("commits")
                    .description("List of commits in the change log")
                    .type(listType(gitUICommitGQLType.typeRef))
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitChangeLog.loadCommits {
                            gitService.getChangeLogCommits(gitChangeLog)
                        }.commits
                    }
            }
            // Checking if a change log has issues
            .field {
                it.name("hasIssues")
                    .description("Checking if a change log can have issues")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitService.getProjectConfiguration(gitChangeLog.project) != null
                    }
            }
            // Issues
            .field { f ->
                f.name("issues")
                    .description("List of issues in the change log")
                    .type(gitChangeLogIssuesGQLType.typeRef)
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitService.getChangeLogIssues(gitChangeLog)
                    }
            }
            // File changes
            .field {
                it.name("files")
                    .description("List of files changes")
                    .type(gqlTypeGitChangeLogFiles.typeRef.toNotNull())
                    .dataFetcher { env ->
                        val gitChangeLog: GitChangeLog = env.getSource()!!
                        gitService.getChangeLogFiles(gitChangeLog)
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
                        val gitChangeLog: GitChangeLog = env.getSource()!!
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
                        val configuredIssueService = gitConfiguration.configuredIssueService
                            ?: return@dataFetcher null
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
            // Dependency change log
            .field {
                it.name("depChangeLog")
                    .description("Gets the change of some dependency")
                    .type(GraphQLTypeReference(typeName)) // Recursive
                    .argument(stringArgument(DEP_CHANGE_LOG_PROJECT, "Name of the project to follow", nullable = false))
                    .dataFetcher { env ->
                        val projectName: String = env.getArgument(DEP_CHANGE_LOG_PROJECT)!!
                        val baseChangeLog: GitChangeLog = env.getSource()!!
                        val buildFrom: Build = baseChangeLog.scmBuildFrom.buildView.build
                        val buildTo: Build = baseChangeLog.scmBuildTo.buildView.build
                        // Dependency change log
                        val depBoundaries = recursiveChangeLogService.getDependencyChangeLog(
                            buildFrom, buildTo, projectName
                        )
                        if (depBoundaries != null) {
                            val (depFirst, depLast) = depBoundaries
                            gitService.changeLog(
                                BuildDiffRequest(
                                    depFirst.id,
                                    depLast.id
                                )
                            ).apply {
                                gitChangeLogCache.put(this)
                            }
                        } else {
                            // No change log
                            null
                        }
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
        const val DEP_CHANGE_LOG_PROJECT = "project"
    }
}