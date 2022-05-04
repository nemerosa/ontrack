package net.nemerosa.ontrack.extension.github.ingestion.validation

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Service

@Service
class DefaultIngestionValidateDataService(
    private val storage: IngestionHookPayloadStorage,
    private val queue: IngestionHookQueue,
) : IngestionValidateDataService {
    override fun ingestValidationData(input: AbstractGitHubIngestionValidateDataInput) {
        // Creates the payload
        val payload = IngestionHookPayload(
            gitHubDelivery = "",
            gitHubEvent = IngestionValidateDateEventProcessor.EVENT,
            gitHubHookID = 0,
            gitHubHookInstallationTargetID = 0,
            gitHubHookInstallationTargetType = "",
            payload = input.toPayload().asJson(),
            repository = Repository.stub(
                owner = input.owner,
                name = input.repository,
            ),
        )
        // Stores it
        storage.store(payload)
        // Pushes it on the queue
        queue.queue(payload)
    }
}