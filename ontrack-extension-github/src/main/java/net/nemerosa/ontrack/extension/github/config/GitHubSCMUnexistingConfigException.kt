package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubSCMUnexistingConfigException : InputException(
    "Could not find any matching GitHub SCM configuration."
)