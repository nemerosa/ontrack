package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import java.util.*

interface IngestionLinksService {

    /**
     * Scheduling the ingestion of links for a build.
     */
    fun ingestLinks(input: AbstractGitHubIngestionLinksInput): UUID

}