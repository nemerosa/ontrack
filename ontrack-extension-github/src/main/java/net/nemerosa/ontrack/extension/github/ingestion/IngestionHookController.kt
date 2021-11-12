package net.nemerosa.ontrack.extension.github.ingestion

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.metrics.INGESTION_METRIC_EVENT_TAG
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.payload.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsMissingTokenException
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
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
    private val meterRegistry: MeterRegistry,
    private val cachedSettingsService: CachedSettingsService,
    ingestionEventProcessors: List<IngestionEventProcessor>,
) {

    private val eventProcessors = ingestionEventProcessors.associateBy { it.event }

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
        // Gets the event processor if any
        val eventProcessor =
            eventProcessors[gitHubEvent] ?: throw GitHubIngestionHookEventNotSupportedException(gitHubEvent)
        // Checking the signature
        val json = when (ingestionHookSignatureService.checkPayloadSignature(body, signature)) {
            IngestionHookSignatureCheckResult.MISMATCH -> {
                meterRegistry.increment(
                    IngestionMetrics.SIGNATURE_ERROR_COUNT,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
                )
                throw GitHubIngestionHookSignatureMismatchException()
            }
            IngestionHookSignatureCheckResult.MISSING_TOKEN -> throw GitHubIngestionSettingsMissingTokenException()
            IngestionHookSignatureCheckResult.OK -> body.parseAsJson()
        }
        // Getting the repository
        val repository = if (json.has("repository")) {
            json.get("repository").parse<Repository>()
        } else {
            null
        }
        // Repository-based filter
        if (repository != null) {
            val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
            if (FilterHelper.excludes(
                    repository.name,
                    settings.repositoryIncludes,
                    settings.repositoryExcludes
                )
            ) {
                return IngestionHookResponse(
                    message = "Ingestion request for event $gitHubEvent and repository ${repository.fullName} has been received correctly but won't be processed because of the exclusion rules",
                    uuid = null,
                    event = gitHubEvent,
                    processing = false,
                )
            }
        }
        // Creates the payload object
        val payload = IngestionHookPayload(
            gitHubDelivery = gitHubDelivery,
            gitHubEvent = gitHubEvent,
            gitHubHookID = gitHubHookID,
            gitHubHookInstallationTargetID = gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = gitHubHookInstallationTargetType,
            payload = json,
            repository = repository,
        )
        // Pre-sorting
        return when (eventProcessor.preProcessingCheck(payload)) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED -> securityService.asAdmin {
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
            IngestionEventPreprocessingCheck.IGNORED -> IngestionHookResponse(
                message = "Ingestion request ${payload.uuid}/${payload.gitHubEvent} has been received correctly but won't be processed.",
                uuid = payload.uuid,
                event = payload.gitHubEvent,
                processing = false,
            )
        }
    }

    class IngestionHookResponse(
        val message: String,
        val uuid: UUID?,
        val event: String,
        val processing: Boolean,
    )

}