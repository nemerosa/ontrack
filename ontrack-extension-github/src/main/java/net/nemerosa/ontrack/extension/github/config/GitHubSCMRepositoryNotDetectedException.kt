package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubSCMRepositoryNotDetectedException(scmUrl: String) : InputException(
    "Could not find any GitHub repository for SCM URL: $scmUrl"
)