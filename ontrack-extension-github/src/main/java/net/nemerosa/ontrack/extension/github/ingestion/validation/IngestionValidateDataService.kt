package net.nemerosa.ontrack.extension.github.ingestion.validation

interface IngestionValidateDataService {

    fun ingestValidationData(input: AbstractGitHubIngestionValidateDataInput)

}