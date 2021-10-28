package net.nemerosa.ontrack.extension.github.ingestion.processing

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueueSubscriber
import org.springframework.stereotype.Component

/**
 * Processes hook payloads.
 */
@Component
class IngestionHookProcessor(
    private val ingestionHookProcessingService: IngestionHookProcessingService,
) : IngestionHookQueueSubscriber {

    override fun onIngestionHookPayload(payload: IngestionHookPayload) {
        // TODO Remove the coroutine when switching to queue processing
        runBlocking {
            launch(Job()) {
                ingestionHookProcessingService.process(payload)
            }
        }
    }

}