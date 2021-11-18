package net.nemerosa.ontrack.kdsl.spec.extension.github

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
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
     * @return Saved configuration, with obfuscated values but for the name & URL.
     */
    fun createConfig(config: GitHubConfiguration): GitHubConfiguration = graphqlConnector.mutate(
        mutation = config.run {
            CreateGitHubConfigurationMutation(
                name,
                Input.fromNullable(url),
                Input.fromNullable(user),
                Input.fromNullable(password),
                Input.fromNullable(oauth2Token),
                Input.fromNullable(appId),
                Input.fromNullable(appPrivateKey),
                Input.fromNullable(appInstallationAccountName),
            )
        }
    ) {
        it?.createGitHubConfiguration()?.fragments()?.payloadUserErrors()?.convert()
    }?.checkData {
        it.createGitHubConfiguration()?.configuration()
    }?.let {
        GitHubConfiguration(
            name = config.name,
            url = it.url(),
            user = null,
            password = null,
            oauth2Token = null,
            appId = null,
            appPrivateKey = null,
            appInstallationAccountName = null,
        )
    } ?: throw GraphQLMissingDataException("Did not get back the created GitHub configuration.")

}