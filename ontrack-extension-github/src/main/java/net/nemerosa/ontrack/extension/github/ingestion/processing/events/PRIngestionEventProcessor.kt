package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.pr.PRPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.pr.PRPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.pr.PRPayloadListenerCheck
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class PRIngestionEventProcessor(
    structureService: StructureService,
    private val prPayloadListeners: List<PRPayloadListener>,
) : AbstractRepositoryIngestionEventProcessor<PRPayload>(
    structureService
) {

    override val event: String = "pull_requesr"

    override val payloadType: KClass<PRPayload> = PRPayload::class

    override fun preProcessingCheck(payload: PRPayload): IngestionEventPreprocessingCheck {
        val checks = prPayloadListeners.map { listener ->
            listener.preProcessCheck(payload)
        }
        return if (checks.contains(PRPayloadListenerCheck.TO_BE_PROCESSED)) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED
        } else {
            IngestionEventPreprocessingCheck.IGNORED
        }
    }

    override fun process(payload: PRPayload, configuration: String?): IngestionEventProcessingResult {
        prPayloadListeners.map { listener ->
            listener.process(payload, configuration)
        }
        return IngestionEventProcessingResult.PROCESSED
    }

}

