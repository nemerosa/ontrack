package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.BaseException

class GitHubConfigURLSeveralMatchesException(url: String) : BaseException(
    "The URL of the workflow - $url - matches several GitHub configurations in Ontrack."
)
