package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.SCMCommitInfo
import net.nemerosa.ontrack.extension.scm.SCMIssueInfo
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMDecoratedCommit
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

/**
 * Contributes the `scmInfoInfo` to the `Project` type.
 */
@Component
class SCMIssueInfoProjectGraphQLFieldContributor(
    private val gqlTypeSCMIssueInfo: GQLTypeSCMIssueInfo,
    private val scmDetector: SCMDetector,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("scmIssueInfo")
                    .description("SCM information about an issue")
                    .argument(stringArgument(ARG_ISSUE_KEY, "Key of the issue", nullable = false))
                    .type(gqlTypeSCMIssueInfo.typeRef)
                    .dataFetcher { env ->
                        val project: Project = env.getSource()!!
                        val issueKey: String = env.getArgument(ARG_ISSUE_KEY)!!
                        getSCMIssueInfo(project, issueKey)
                    }
                    .build()
            )
        } else {
            null
        }

    private fun getSCMIssueInfo(project: Project, issueKey: String): SCMIssueInfo? {
        val scm = scmDetector.getSCM(project) ?: return null
        return if (scm is SCMChangeLogEnabled) {
            val configuredIssueService = scm.getConfiguredIssueService() ?: return null
            val issue = configuredIssueService.getIssue(issueKey) ?: return null
            // Looking for the last commit of this issue
            val commitId: String? = configuredIssueService.getLastCommit(
                issue,
                scm.issueRepositoryContext,
                )
            val scmCommitInfo: SCMCommitInfo? = if (commitId != null) {
                val commit = scm.getCommit(commitId)
                if (commit != null) {
                    SCMCommitInfo(
                        scmDecoratedCommit = SCMDecoratedCommit(
                            project = project,
                            commit = commit,
                        )
                    )
                } else {
                    null
                }
            } else {
                null
            }
            // OK
            return SCMIssueInfo(
                issueServiceConfigurationRepresentation = configuredIssueService.issueServiceConfigurationRepresentation,
                issue = issue,
                scmCommitInfo = scmCommitInfo,
            )
        } else {
            null
        }
    }

    companion object {
        const val ARG_ISSUE_KEY = "issueKey"
    }
}