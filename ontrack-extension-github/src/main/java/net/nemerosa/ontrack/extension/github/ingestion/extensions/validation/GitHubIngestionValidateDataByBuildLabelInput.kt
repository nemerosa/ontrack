package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

/**
 * Input for the data validation for a build identified by its label
 */
@APIName("GitHubIngestionValidateDataByBuildLabelInput")
@APIDescription("Input for the data validation for a build identified by its label")
class GitHubIngestionValidateDataByBuildLabelInput(
    owner: String,
    repository: String,
    // ref: String,
    validation: String,
    validationData: GitHubIngestionValidationDataInput,
    validationStatus: String?,
    @APIDescription("Build release property (label)")
    val buildLabel: String,
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
        buildLabel = buildLabel,
    )
}