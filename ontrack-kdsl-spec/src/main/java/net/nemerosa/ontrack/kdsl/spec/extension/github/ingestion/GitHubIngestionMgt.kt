package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import net.nemerosa.ontrack.kdsl.spec.support.PaginatedList

/**
 * Management of the GitHub ingestion.
 */
class GitHubIngestionMgt {

    /**
     * Getting a list of payloads.
     */
    fun payloads(
        offset: Int = 0,
        size: Int = 10,
        statuses: List<String>? = null,
        gitHubEvent: String? = null,
        repository: String? = null,
    ): PaginatedList<GitHubIngestionPayload> = TODO()

}