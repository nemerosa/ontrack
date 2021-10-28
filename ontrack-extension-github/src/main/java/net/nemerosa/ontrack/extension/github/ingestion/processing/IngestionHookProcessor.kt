package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueueSubscriber
import org.springframework.stereotype.Component

/**
 * Processes hook payloads.
 */
@Component
class IngestionHookProcessor : IngestionHookQueueSubscriber {

    override fun onIngestionHookPayload(payload: IngestionHookPayload) {
        TODO("Not yet implemented")
    }

}