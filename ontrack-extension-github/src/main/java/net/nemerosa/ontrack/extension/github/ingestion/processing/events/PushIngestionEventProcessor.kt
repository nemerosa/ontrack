package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
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

    override fun process(payload: PushPayload, configuration: String?): IngestionEventProcessingResultDetails {
        var outcome = IngestionEventProcessingResultDetails.empty
        pushPayloadListeners.map { listener ->
            if (listener.preProcessCheck(payload) == PushPayloadListenerCheck.TO_BE_PROCESSED) {
                outcome += listener.process(payload, configuration)
            }
        }
        return outcome
    }

    /**
     * Tag, or path if only one, or branch#commit
     */
    override fun getPayloadSource(payload: PushPayload): String? =
        payload.getTag()
            ?: if (payload.commits.size == 1) {
                val commit = payload.commits.first()
                val paths = commit.paths()
                if (paths.size == 1) {
                    paths.first()
                } else {
                    branchCommit(payload)
                }
            } else {
                branchCommit(payload)
            }

    private fun branchCommit(payload: PushPayload) =
        if (payload.headCommit != null) {
            "${payload.branchName}@${payload.headCommit.id}"
        } else {
            payload.branchName
        }

}

