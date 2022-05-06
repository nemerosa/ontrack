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
) {
    fun getSource() = if (runId != null) {
        "$validation@run=$runId"
    } else if (buildName != null) {
        "$validation@name=$buildName"
    } else if (buildLabel != null) {
        "$validation@label=$buildLabel"
    } else {
        validation
    }
}