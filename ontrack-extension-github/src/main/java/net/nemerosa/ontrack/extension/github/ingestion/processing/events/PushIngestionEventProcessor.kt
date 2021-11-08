package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerOutcome
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class PushIngestionEventProcessor(
    structureService: StructureService,
    private val pushPayloadListeners: List<PushPayloadListener>,
) : AbstractRepositoryIngestionEventProcessor<PushPayload>(
    structureService
) {

    override val event: String = "push"

    override val payloadType: KClass<PushPayload> = PushPayload::class

    override fun process(payload: PushPayload): IngestionEventProcessingResult {
        val outcomes = pushPayloadListeners.map { listener ->
            listener.process(payload)
        }
        return if (outcomes.contains(PushPayloadListenerOutcome.PROCESSED)) {
            IngestionEventProcessingResult.PROCESSED
        } else {
            IngestionEventProcessingResult.IGNORED
        }
    }

}

