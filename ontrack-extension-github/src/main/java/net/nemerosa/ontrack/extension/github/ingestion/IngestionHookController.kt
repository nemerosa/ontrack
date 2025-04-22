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
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettingsMissingTokenException
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.model.structure.checkTokenForSecurityContext
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
    private val tokensService: TokensService,
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
        @RequestParam(value = "configuration", required = false) configuration: String?,
    ): IngestionHookResponse {
        // Checking if the hook is enabled
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        if (!settings.enabled) {
            throw GitHubIngestionHookDisabledException()
        }
        // Gets the event processor if any
        val eventProcessor =
            eventProcessors[gitHubEvent] ?: throw GitHubIngestionHookEventNotSupportedException(gitHubEvent)
        // Checking the signature
        val json = when (ingestionHookSignatureService.checkPayloadSignature(body, signature)) {

            IngestionHookSignatureCheckResult.MISMATCH -> {
                meterRegistry.increment(
                    IngestionMetrics.Hook.signatureErrorCount,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
                )
                throw GitHubIngestionHookSignatureMismatchException()
            }

            IngestionHookSignatureCheckResult.MISSING_TOKEN -> throw GitHubIngestionSettingsMissingTokenException()
            IngestionHookSignatureCheckResult.OK -> body.parseAsJson()
        }
        // Setting the user security context
        tokensService.checkTokenForSecurityContext(
            token = settings.token,
            message = "Token is denied"
        )
        // Getting the repository
        val repository = if (json.has("repository")) {
            json.get("repository").parse<Repository>()
        } else {
            null
        }
        // Repository-based filter
        if (repository != null) {
            if (FilterHelper.excludes(
                    repository.name,
                    settings.repositoryIncludes,
                    settings.repositoryExcludes
                )
            ) {
                meterRegistry.increment(
                    IngestionMetrics.Hook.repositoryRejectedCount,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
                )
                return IngestionHookResponse(
                    message = "Ingestion request for event $gitHubEvent and repository ${repository.fullName} has been received correctly but won't be processed because of the exclusion rules",
                    uuid = null,
                    event = gitHubEvent,
                    processing = false,
                )
            } else {
                meterRegistry.increment(
                    IngestionMetrics.Hook.repositoryAcceptedCount,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
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
            configuration = configuration,
            accountName = securityService.currentUser?.name
                ?: error("Missing account name to process the payload")
        )
        // Pre-sorting
        return when (eventProcessor.preProcessingCheck(payload)) {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED -> {
                meterRegistry.increment(
                    IngestionMetrics.Hook.acceptedCount,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
                )
                // Stores it
                storage.store(payload, eventProcessor.getPayloadSource(payload))
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

            IngestionEventPreprocessingCheck.IGNORED -> {
                meterRegistry.increment(
                    IngestionMetrics.Hook.ignoredCount,
                    INGESTION_METRIC_EVENT_TAG to gitHubEvent,
                )
                IngestionHookResponse(
                    message = "Ingestion request ${payload.uuid}/${payload.gitHubEvent} has been received correctly but won't be processed.",
                    uuid = payload.uuid,
                    event = payload.gitHubEvent,
                    processing = false,
                )
            }
        }
    }

    class IngestionHookResponse(
        val message: String,
        val uuid: UUID?,
        val event: String,
        val processing: Boolean,
    )

}