package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import java.util.*

interface IngestionValidateDataService {

    /**
     * Schedules the processing of the validation data
     *
     * @return The UUID of the processed payload
     */
    fun ingestValidationData(input: AbstractGitHubIngestionValidateDataInput): UUID

}