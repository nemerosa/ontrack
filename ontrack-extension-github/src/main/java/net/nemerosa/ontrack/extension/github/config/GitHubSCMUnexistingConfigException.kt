package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubSCMUnexistingConfigException(scmUrl: String) : InputException(
    "Could not find any matching GitHub SCM configuration using: $scmUrl"
)