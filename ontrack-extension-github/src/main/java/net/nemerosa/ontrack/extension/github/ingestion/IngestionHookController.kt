package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureService
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

/**
 * Hook to register in GitHub.
 */
@RestController("/extension/github/hook")
class IngestionHookController(
    private val queue: IngestionHookQueue,
    private val storage: IngestionHookPayloadStorage,
    private val ingestionHookSignatureService: IngestionHookSignatureService,
) {

    @PostMapping("/")
    fun hook(
        @RequestBody body: String,
        @RequestHeader("X-GitHub-Delivery") gitHubDelivery: String,
        @RequestHeader("X-GitHub-Event") gitHubEvent: String,
        @RequestHeader("X-GitHub-Hook-ID") gitHubHookID: Int,
        @RequestHeader("X-GitHub-Hook-Installation-Target-ID") gitHubHookInstallationTargetID: Int,
        @RequestHeader("X-GitHub-Hook-Installation-Target-Type") gitHubHookInstallationTargetType: String,
        @RequestHeader("X-Hub-Signature-256") signature: String,
    ) {
        // Checking the signature
        val json = ingestionHookSignatureService.checkPayloadSignature(body, signature)
        // Creates the payload object
        val payload = IngestionHookPayload(
            gitHubDelivery = gitHubDelivery,
            gitHubEvent = gitHubEvent,
            gitHubHookID = gitHubHookID,
            gitHubHookInstallationTargetID = gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = gitHubHookInstallationTargetType,
            payload = json,
        )
        // Stores it
        storage.store(payload)
        // Pushes it on the queue
        queue.queue(payload)
    }

}