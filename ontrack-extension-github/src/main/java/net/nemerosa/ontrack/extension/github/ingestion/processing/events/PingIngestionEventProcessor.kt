package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class PingIngestionEventProcessor : AbstractIngestionEventProcessor<PingPayload>() {

    /**
     * Not doing anything
     */
    override fun process(payload: PingPayload, configuration: String?): IngestionEventProcessingResult =
        IngestionEventProcessingResult.IGNORED

    override val payloadType: KClass<PingPayload> = PingPayload::class

    override val event: String = "ping"

    /**
     * Pings are acknowledged but not processed.
     */
    override fun preProcessingCheck(payload: PingPayload): IngestionEventPreprocessingCheck =
        IngestionEventPreprocessingCheck.IGNORED
}

class PingPayload(repository: Repository) : AbstractRepositoryPayload(repository)