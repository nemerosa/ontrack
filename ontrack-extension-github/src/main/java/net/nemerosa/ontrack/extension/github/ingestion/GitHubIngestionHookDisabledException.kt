package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.BaseException

class GitHubIngestionHookDisabledException : BaseException(
    "GitHub ingestion is currently disabled."
)