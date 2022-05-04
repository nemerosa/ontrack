package net.nemerosa.ontrack.extension.github.ingestion.validation

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

/**
 * Input for the data validation for a build identified by its name
 */
@APIName("GitHubIngestionValidateDataByBuildNameInput")
@APIDescription("Input for the data validation for a build identified by its name")
class GitHubIngestionValidateDataByBuildNameInput(
    owner: String,
    repository: String,
    // ref: String,
    validation: String,
    validationData: GitHubIngestionValidationDataInput,
    validationStatus: String?,
    @APIDescription("Build name")
    val buildName: String,
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
        buildName = buildName,
    )
}