package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookSignatureService
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Hook to register in GitHub.
 */
@RestController
@RequestMapping("/hook/secured/github/ingestion")
class IngestionHookController(
    private val queue: IngestionHookQueue,
    private val storage: IngestionHookPayloadStorage,
    private val ingestionHookSignatureService: IngestionHookSignatureService,
    private val securityService: SecurityService,
) {

    @PostMapping("")
    fun hook(
        @RequestBody body: String,
        @RequestHeader("X-GitHub-Delivery") gitHubDelivery: String,
        @RequestHeader("X-GitHub-Event") gitHubEvent: String,
        @RequestHeader("X-GitHub-Hook-ID") gitHubHookID: Int,
        @RequestHeader("X-GitHub-Hook-Installation-Target-ID") gitHubHookInstallationTargetID: Int,
        @RequestHeader("X-GitHub-Hook-Installation-Target-Type") gitHubHookInstallationTargetType: String,
        @RequestHeader("X-Hub-Signature-256") signature: String,
    ): IngestionHookResponse {
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
        // Pre-sorting
        val toBeProcessed = preFlightCheck(payload)
        if (toBeProcessed) {
            return securityService.asAdmin {
                // Stores it
                storage.store(payload)
                // Pushes it on the queue
                queue.queue(payload)
                // Ok
                IngestionHookResponse(
                    message = "Ingestion request ${payload.uuid}/${payload.gitHubEvent} has been received and is processed in the background.",
                    uuid = payload.uuid,
                    event = payload.gitHubEvent,
                    processing = true,
                )
            }
        } else {
            return IngestionHookResponse(
                message = "Ingestion request ${payload.uuid}/${payload.gitHubEvent} has been received correctly but won't be processed.",
                uuid = payload.uuid,
                event = payload.gitHubEvent,
                processing = false,
            )
        }
    }

    private fun preFlightCheck(payload: IngestionHookPayload) = when (payload.gitHubEvent) {
        "ping" -> false
        "workflow_job" -> true
        "workflow_run" -> true
        else -> throw GitHubIngestionHookEventNotSupportedException(payload.gitHubEvent)
    }

    class IngestionHookResponse(
        val message: String,
        val uuid: UUID,
        val event: String,
        val processing: Boolean,
    )

}