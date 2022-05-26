package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractGitHubIngestionBuildPayload

class GitHubIngestionValidateDataPayload(
    owner: String,
    repository: String,
    val validation: String,
    val validationData: GitHubIngestionValidationDataInput,
    val validationStatus: String?,
    runId: Long? = null,
    buildName: String? = null,
    buildLabel: String? = null,
) : AbstractGitHubIngestionBuildPayload(
    owner,
    repository,
    runId,
    buildName,
    buildLabel
) {
    @JsonIgnore
    override fun getSource() = "$validation@${super.getSource()}"
}