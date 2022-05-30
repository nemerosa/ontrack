package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GitHubIngestionSetBranchConfigMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Branch

fun Branch.setBranchGitHubIngestionConfig(yaml: String) {
    graphqlConnector.mutate(
        GitHubIngestionSetBranchConfigMutation(
            id.toInt(),
            yaml
        )
    ) {
        it?.setBranchGitHubIngestionConfig()?.fragments()?.payloadUserErrors()?.convert()
    }
}