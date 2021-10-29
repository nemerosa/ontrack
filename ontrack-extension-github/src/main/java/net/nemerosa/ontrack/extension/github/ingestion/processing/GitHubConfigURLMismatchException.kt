package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.BaseException

class GitHubConfigURLMismatchException(url: String) : BaseException(
    "The URL of the workflow - $url - does not match the GitHub configuration in Ontrack."
)
