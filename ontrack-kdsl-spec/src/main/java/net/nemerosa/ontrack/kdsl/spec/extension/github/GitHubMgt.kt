package net.nemerosa.ontrack.kdsl.spec.extension.github

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateGitHubConfigurationMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Interface for the management of GitHub in Ontrack.
 */
class GitHubMgt(connector: Connector) : Connected(connector) {

    /**
     * Creates a configuration
     *
     * @param config Configuration to save
     * @return Saved configuration
     */
    fun createConfig(config: GitHubConfiguration): GitHubConfiguration = TODO()
//    fun createConfig(config: GitHubConfiguration): GitHubConfiguration = graphqlConnector.mutate(
//        mutation = config.run {
//            CreateGitHubConfigurationMutation(
//                name = name,
//                url = Input.fromNullable(url),
//                user = Input.fromNullable(user),
//                password = Input.fromNullable(password),
//                oauth2Token = Input.fromNullable(oauth2Token),
//                appId = Input.fromNullable(appId),
//                appPrivateKey = Input.fromNullable(appPrivateKey),
//                appInstallationAccountName = Input.fromNullable(appInstallationAccountName),
//            )
//        }
//    ) {
//        TODO("it.createGitHubConfiguration()?.errors()?.")
//    }

}