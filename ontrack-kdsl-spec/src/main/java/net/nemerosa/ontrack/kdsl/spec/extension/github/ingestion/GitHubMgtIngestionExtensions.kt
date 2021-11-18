package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubMgt

/**
 * Management of the GitHub ingestion.
 */
val GitHubMgt.ingestion: GitHubIngestionMgt
    get() =
        GitHubIngestionMgt(connector)
