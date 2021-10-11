package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.extension.github.app.GitHubAppToken
import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount

fun GitHubAppTokenService.getAppInstallationToken(configuration: GitHubEngineConfiguration): String =
    if (configuration.authenticationType() == GitHubAuthenticationType.APP) {
        getAppInstallationToken(
            configuration.name,
            configuration.appId!!,
            configuration.appPrivateKey!!,
            configuration.appInstallationAccountName
        )
    } else {
        error("This configuration is not using a GitHub App.")
    }

fun GitHubAppTokenService.getAppInstallationTokenInformation(configuration: GitHubEngineConfiguration): GitHubAppToken? =
    if (configuration.authenticationType() == GitHubAuthenticationType.APP) {
        getAppInstallationTokenInformation(
            configuration.name,
            configuration.appId!!,
            configuration.appPrivateKey!!,
            configuration.appInstallationAccountName
        )
    } else {
        null
    }

fun GitHubAppTokenService.getAppInstallationAccount(configuration: GitHubEngineConfiguration): GitHubAppAccount =
    if (configuration.authenticationType() == GitHubAuthenticationType.APP) {
        getAppInstallationAccount(
            configuration.name,
            configuration.appId!!,
            configuration.appPrivateKey!!,
            configuration.appInstallationAccountName
        )
    } else {
        error("This configuration is not using a GitHub App.")
    }
