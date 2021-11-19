package net.nemerosa.ontrack.extension.github.ingestion.payload

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime
import java.util.*

/**
 * Payload for an ingestion to be processed.
 *
 * @property uuid Unique ID for this payload
 * @property timestamp Timestamp of reception for this payload
 * @property gitHubDelivery Mapped to the `X-GitHub-Delivery` header
 * @property gitHubEvent Mapped to the `X-GitHub-Event` header
 * @property gitHubHookID Mapped to the `X-GitHub-Hook-ID` header
 * @property gitHubHookInstallationTargetID Mapped to the `X-GitHub-Hook-Installation-Target-ID` header
 * @property gitHubHookInstallationTargetType Mapped to the `X-GitHub-Hook-Installation-Target-Type` header
 * @property payload JSON payload, raw from GitHub
 * @property repository Repository this payload refers to
 * @property status Status of the processing
 * @property started Timestamp for the start of the processing
 * @property message Status message (exception stack trace in case of error)
 * @property completion Timestamp for the end of the processing
 * @property configuration Name of the GitHub configuration to use
 * @property routing Routing information
 * @property queue Queue information
 */
data class IngestionHookPayload(
    @APIDescription("Unique ID for this payload")
    val uuid: UUID = UUID.randomUUID(),
    @APIDescription("Timestamp of reception for this payload")
    val timestamp: LocalDateTime = Time.now(),
    @APIDescription("Mapped to the `X-GitHub-Delivery` header")
    val gitHubDelivery: String,
    @APIDescription("Mapped to the `X-GitHub-Event` header")
    val gitHubEvent: String,
    @APIDescription("Mapped to the `X-GitHub-Hook-ID` header")
    val gitHubHookID: Int,
    @APIDescription("Mapped to the `X-GitHub-Hook-Installation-Target-ID` header")
    val gitHubHookInstallationTargetID: Int,
    @APIDescription("Mapped to the `X-GitHub-Hook-Installation-Target-Type` header")
    val gitHubHookInstallationTargetType: String,
    @APIDescription("JSON payload, raw from GitHub")
    val payload: JsonNode,
    @APIDescription("Repository this payload refers to")
    val repository: Repository?,
    @APIDescription("Status of the processing")
    val status: IngestionHookPayloadStatus = IngestionHookPayloadStatus.SCHEDULED,
    @APIDescription("Timestamp for the start of the processing")
    val started: LocalDateTime? = null,
    @APIDescription("Status message (exception stack trace in case of error)")
    val message: String? = null,
    @APIDescription("Timestamp for the end of the processing")
    val completion: LocalDateTime? = null,
    @APIDescription("Name of the GitHub configuration to use")
    val configuration: String? = null,
    @APIDescription("Routing information")
    val routing: String? = null,
    @APIDescription("Queue information")
    val queue: String? = null,
)
