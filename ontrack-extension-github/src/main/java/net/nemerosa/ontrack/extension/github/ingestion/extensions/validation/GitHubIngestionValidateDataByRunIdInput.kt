package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

/**
 * Input for the data validation for a build identified by GHA workflow run ID
 */
@APIName("GitHubIngestionValidateDataByRunIdInput")
@APIDescription("Input for the data validation for a build identified by GHA workflow run ID")
class GitHubIngestionValidateDataByRunIdInput(
    owner: String,
    repository: String,
    // ref: String,
    validation: String,
    validationData: GitHubIngestionValidationDataInput,
    validationStatus: String?,
    @APIDescription("ID of the GHA workflow run")
    val runId: Long,
) : AbstractGitHubIngestionValidateDataInput(
    owner,
    repository,
    // ref,
    validation,
    validationData,
    validationStatus,
) {
    override fun toPayload() = GitHubIngestionValidateDataPayload(
        owner = owner,
        repository = repository,
        validation = validation,
        validationData = validationData,
        validationStatus = validationStatus,
        runId = runId,
    )
}