package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionEventService
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultIngestionValidateDataService(
    private val ingestionEventService: IngestionEventService,
) : IngestionValidateDataService {
    override fun ingestValidationData(input: AbstractGitHubIngestionValidateDataInput): UUID {
        val payload = input.toPayload()
        return ingestionEventService.sendIngestionEvent(
            event = IngestionValidateDateEventProcessor.EVENT,
            owner = input.owner,
            repository = input.repository,
            payload = payload.asJson(),
            payloadSource = payload.getSource(),
        )
    }
}