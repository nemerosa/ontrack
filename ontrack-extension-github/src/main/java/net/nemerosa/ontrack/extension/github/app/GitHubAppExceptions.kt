package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.common.BaseException

class GitHubAppNoInstallationException(appId: String): BaseException(
    "GitHub App $appId has not been installed anywhere."
)

class GitHubAppSeveralInstallationsException(appId: String): BaseException(
    "GitHub App $appId has several installations. The installation account name must be specified."
)

class GitHubAppNoInstallationForAccountException(appId: String, appInstallationAccountName: String): BaseException(
    "GitHub App $appId is not installed for account $appInstallationAccountName."
)

class GitHubAppNoTokenException(appId: String): BaseException(
    "Could not get any installation token for the GitHub App $appId"
)
