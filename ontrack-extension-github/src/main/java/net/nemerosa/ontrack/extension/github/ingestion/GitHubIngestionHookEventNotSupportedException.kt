package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubIngestionHookEventNotSupportedException(event: String) : InputException(
    "Hook event $event is not supported."
)