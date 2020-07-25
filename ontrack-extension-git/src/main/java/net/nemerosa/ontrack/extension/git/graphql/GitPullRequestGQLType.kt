package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GitPullRequestGQLType : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Git pull request associated to a branch")
                    .intField(GitPullRequest::id, "PR id")
                    .booleanField(GitPullRequest::isValid, "PR validity - does it exist?")
                    .stringField(GitPullRequest::key, "Display name for the PR")
                    .stringField(GitPullRequest::source, "Source branch")
                    .field {
                        it.name("simpleSource")
                                .description("Name of the source branch, without the refs/heads/ prefix")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    GitPullRequest.simpleBranchName(env.getSource<GitPullRequest>().source)
                                }
                    }
                    .stringField(GitPullRequest::target, "Target branch")
                    .field {
                        it.name("simpleTarget")
                                .description("Name of the target branch, without the refs/heads/ prefix")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    GitPullRequest.simpleBranchName(env.getSource<GitPullRequest>().target)
                                }
                    }
                    .stringField(GitPullRequest::title, "PR title")
                    .stringField(GitPullRequest::status, "PR status")
                    .stringField(GitPullRequest::url, "Link to the PR web page")
                    .build()

    override fun getTypeName(): String = GitPullRequest::class.java.simpleName
}