package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeGitHubEngineConfiguration(
    private val fieldContributors: List<GQLFieldContributor>,
) : GQLType {

    override fun getTypeName(): String = GitHubEngineConfiguration::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Configuration to connect to GitHub")
        .stringField(
            "name",
            "Name of the configuration"
        )
        .stringField(
            GitHubEngineConfiguration::url,
            "URL to GitHub"
        )
        .stringField(
            "user",
            "User used to connect to GitHub. Can be used with a password or an OAuth2 token. Not needed if using only the OAUth2 token or a GitHub App."
        )
        .stringField(
            GitHubEngineConfiguration::appId,
            "ID of the GitHub App used for authentication"
        )
        .stringField(
            GitHubEngineConfiguration::appInstallationAccountName,
            "Name of the account where the GitHub App is installed."
        )
        .fields(GitHubEngineConfiguration::class.java.graphQLFieldContributions(fieldContributors))
        .build()
}