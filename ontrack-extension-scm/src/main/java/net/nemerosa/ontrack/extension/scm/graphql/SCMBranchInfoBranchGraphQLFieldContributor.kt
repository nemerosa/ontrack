package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.service.SCMBranchInfo
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class SCMBranchInfoBranchGraphQLFieldContributor(
    private val gqlTypeSCMBranchInfo: GQLTypeSCMBranchInfo,
    private val scmDetector: SCMDetector,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BRANCH) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("scmBranchInfo")
                    .description("SCM information about the branch")
                    .type(gqlTypeSCMBranchInfo.typeRef)
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()
                        getSCMBranchInfo(branch)
                    }
                    .build()
            )
        } else {
            null
        }

    private fun getSCMBranchInfo(branch: Branch): SCMBranchInfo? {
        val scm = scmDetector.getSCM(branch.project) ?: return null
        val scmBranch = scm.getSCMBranch(branch) ?: return null
        return SCMBranchInfo(
            type = scm.type,
            engine = scm.engine,
            uri = scm.repositoryURI,
            branch = scmBranch,
            changeLogs = scm is SCMChangeLogEnabled,
        )
    }
}