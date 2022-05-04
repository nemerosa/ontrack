package net.nemerosa.ontrack.extension.github.ingestion.validation

data class GitHubIngestionValidateDataPayload(
    val owner: String,
    val repository: String,
    val validation: String,
    val validationData: GitHubIngestionValidationDataInput,
    val validationStatus: String?,
    val runId: Long? = null,
    val buildName: String? = null,
    val buildLabel: String? = null,
)