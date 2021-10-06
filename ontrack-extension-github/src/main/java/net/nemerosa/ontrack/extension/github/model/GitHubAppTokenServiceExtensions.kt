package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService

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
