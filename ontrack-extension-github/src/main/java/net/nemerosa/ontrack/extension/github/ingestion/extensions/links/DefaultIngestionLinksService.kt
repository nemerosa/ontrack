package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionEventService
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultIngestionLinksService(
    private val ingestionEventService: IngestionEventService,
) : IngestionLinksService {
    override fun ingestLinks(input: AbstractGitHubIngestionLinksInput): UUID {
        val payload = input.toPayload()
        return ingestionEventService.sendIngestionEvent(
            event = IngestionLinksEventProcessor.EVENT,
            owner = input.owner,
            repository = input.repository,
            payload = payload.asJson(),
            payloadSource = payload.getSource(),
        )
    }
}