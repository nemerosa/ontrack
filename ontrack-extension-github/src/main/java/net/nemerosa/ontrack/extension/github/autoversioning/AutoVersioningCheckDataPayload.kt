package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractGitHubIngestionBuildPayload

class AutoVersioningCheckDataPayload(
    owner: String,
    repository: String,
    runId: Long? = null,
    buildName: String? = null,
    buildLabel: String? = null,
) : AbstractGitHubIngestionBuildPayload(
    owner = owner,
    repository = repository,
    runId = runId,
    buildName = buildName,
    buildLabel = buildLabel
)
