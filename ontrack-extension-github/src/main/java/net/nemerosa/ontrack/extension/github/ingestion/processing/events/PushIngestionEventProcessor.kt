package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
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

    override fun preProcessingCheck(payload: PushPayload): IngestionEventPreprocessingCheck {
        val checks = pushPayloadListeners.map { listener ->
            listener.preProcessCheck(payload)
        }
        return if (checks.contains(PushPayloadListenerCheck.TO_BE_PROCESSED)) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED
        } else {
            IngestionEventPreprocessingCheck.IGNORED
        }
    }

    override fun process(payload: PushPayload): IngestionEventProcessingResult {
        pushPayloadListeners.map { listener ->
            listener.process(payload)
        }
        return IngestionEventProcessingResult.PROCESSED
    }

}

