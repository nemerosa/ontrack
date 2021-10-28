package net.nemerosa.ontrack.extension.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Hook to register in GitHub.
 */
@RestController("/extension/github/hook")
class IngestionHookController(
    private val queue: IngestionHookQueue,
    private val storage: IngestionHookPayloadStorage,
) {

    @PostMapping("/")
    fun hook(
        @RequestBody body: JsonNode,
    ) {
        // Creates the payload object
        val payload = IngestionHookPayload(payload = body)
        // Stores it
        storage.store(payload)
        // Pushes it on the queue
        queue.queue(payload)
    }

}