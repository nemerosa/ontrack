package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.SCMCommitInfo
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
 * Contributes the `scmCommitInfo` to the `Project` type.
 */
@Component
class SCMCommitInfoProjectGraphQLFieldContributor(
    private val gqlTypeSCMCommitInfo: GQLTypeSCMCommitInfo,
    private val scmDetector: SCMDetector,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("scmCommitInfo")
                    .description("SCM information about a commit")
                    .argument(stringArgument(ARG_COMMIT_ID, "ID of the commit", nullable = false))
                    .type(gqlTypeSCMCommitInfo.typeRef)
                    .dataFetcher { env ->
                        val project: Project = env.getSource()!!
                        val commitId: String = env.getArgument(ARG_COMMIT_ID)!!
                        getSCMCommitInfo(project, commitId)
                    }
                    .build()
            )
        } else {
            null
        }

    private fun getSCMCommitInfo(project: Project, commitId: String): SCMCommitInfo? {
        val scm = scmDetector.getSCM(project) ?: return null
        return if (scm is SCMChangeLogEnabled) {
            val commit = scm.getCommit(project = project, id = commitId)
            if (commit != null) {
                SCMCommitInfo(
                    scmDecoratedCommit = SCMDecoratedCommit(
                        project = project,
                        commit = commit,
                    ),
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    companion object {
        const val ARG_COMMIT_ID = "commitId"
    }
}