package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.model.exceptions.InputException

class GitHubIngestionHookSignatureMismatchException : InputException(
    """Hook payload signature does not match."""
)
