package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBranch
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GitPullRequestGQLType(
        private val gitService: GitService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Git pull request associated to a branch")
                    .intField(Data::id, "PR id")
                    .booleanField(Data::isValid, "PR validity - does it exist?")
                    .stringField(Data::key, "Display name for the PR")
                    .stringField(Data::source, "Source branch")
                    .stringField(Data::target, "Target branch")
                    .stringField(Data::title, "PR title")
                    .stringField(Data::status, "PR status")
                    .stringField(Data::url, "Link to the PR web page")
                    // Source branch
                    .field {
                        it.name("sourceBranch")
                                .description("Link to the Ontrack branch which is the source of this PR")
                                .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
                                .dataFetcher { env ->
                                    val data: Data = env.getSource()
                                    findBranchByGitBranch(data.parentBranch.project, data.pr.source)
                                }
                    }
                    // Target branch
                    .field {
                        it.name("targetBranch")
                                .description("Link to the Ontrack branch which is the target of this PR")
                                .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
                                .dataFetcher { env ->
                                    val data: Data = env.getSource()
                                    findBranchByGitBranch(data.parentBranch.project, data.pr.target)
                                }
                    }
                    // OK
                    .build()

    private fun findBranchByGitBranch(project: Project, gitBranch: String): Branch? {
        return gitService.findBranchWithGitBranch(project, gitBranch)
    }

    override fun getTypeName(): String = GitPullRequest::class.java.simpleName

    class Data(
            val parentBranch: Branch,
            val pr: GitPullRequest
    ) {
        val id: Int = pr.id
        val isValid: Boolean = pr.isValid
        val key: String = pr.key
        val source: String = pr.source
        val target: String = pr.target
        val title: String = pr.title
        val status: String = pr.status
        val url: String = pr.url
    }
}