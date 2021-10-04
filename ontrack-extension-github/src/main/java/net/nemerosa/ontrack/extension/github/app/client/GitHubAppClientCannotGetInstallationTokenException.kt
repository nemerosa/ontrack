package net.nemerosa.ontrack.extension.github.app.client

import net.nemerosa.ontrack.common.BaseException

class GitHubAppClientCannotGetInstallationTokenException(appInstallationId: String) : BaseException(
    "Cannot get an app installation token for app installation $appInstallationId"
)
