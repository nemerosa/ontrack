package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.BaseException

class GitHubConfigURLNoMatchException(url: String) : BaseException(
    "The URL of the workflow - $url - does not match any GitHub configuration in Ontrack."
)
