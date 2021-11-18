package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubConfigProvidedNameNotFoundException(
    configurationName: String
) : InputException("""The required configuration, with name "$configurationName", cannot be found.""")
