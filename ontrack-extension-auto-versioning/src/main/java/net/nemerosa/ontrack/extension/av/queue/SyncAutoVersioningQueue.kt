package net.nemerosa.ontrack.extension.av.queue

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Synchronous processing, used for testing.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.auto-versioning.queue",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncAutoVersioningQueue(
    private val autoVersioningProcessingService: AutoVersioningProcessingService,
) : AutoVersioningQueue {

    override fun queue(order: AutoVersioningOrder) {
        runBlocking {
            launch(Job()) {
                autoVersioningProcessingService.process(order)
            }
        }
    }

}