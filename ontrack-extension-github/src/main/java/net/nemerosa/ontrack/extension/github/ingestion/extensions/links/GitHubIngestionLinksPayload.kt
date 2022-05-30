package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractGitHubIngestionBuildPayload
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format

class GitHubIngestionLinksPayload(
    owner: String,
    repository: String,
    runId: Long? = null,
    buildName: String? = null,
    buildLabel: String? = null,
    val buildLinks: List<GitHubIngestionLink>,
    val addOnly: Boolean,
) : AbstractGitHubIngestionBuildPayload(
    owner, repository, runId, buildName, buildLabel
) {
    companion object {
        const val ADD_ONLY_DEFAULT = false
    }

    override fun toString(): String = asJson().format()
}
