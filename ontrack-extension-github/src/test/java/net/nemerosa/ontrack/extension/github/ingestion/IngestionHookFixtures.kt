package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRun
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunAction
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.json.asJson
import java.time.LocalDateTime
import java.util.*

object IngestionHookFixtures {

    /**
     * Workflow run payload
     */
    fun workflowRunPayload(
        runId: Long = 1,
        action: WorkflowRunAction,
        runNumber: Int,
        headBranch: String,
        createdAtDate: LocalDateTime = Time.now(),
        repoName: String,
        repoDescription: String = "Repository $repoName",
        owner: String,
        sender: String,
        commit: String,
        htmlUrl: String = "https://github.com/nemerosa/github-ingestion-poc/actions/runs/1395528922",
    ) = WorkflowRunPayload(
        action = action,
        workflowRun = WorkflowRun(
            id = runId,
            name = "CI",
            runNumber = runNumber,
            pullRequests = emptyList(),
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
            updatedAtDate = null,
            htmlUrl = htmlUrl,
            event = "push",
        ),
        repository = Repository(
            name = repoName,
            description = repoDescription,
            owner = Owner(login = owner),
        ),
        sender = User(login = sender)
    )

    /**
     * Sample payload
     */
    fun sampleWorkflowRunIngestionPayload(
        timestamp: LocalDateTime = Time.now(),
        message: String? = null,
        status: IngestionHookPayloadStatus = IngestionHookPayloadStatus.SCHEDULED,
    ) = payloadHeaders(event = "workflow_run").run {
        IngestionHookPayload(
            timestamp = timestamp,
            gitHubEvent = gitHubEvent,
            gitHubDelivery = gitHubDelivery,
            gitHubHookID = gitHubHookID,
            gitHubHookInstallationTargetID = gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = gitHubHookInstallationTargetType,
            payload = sampleWorkflowRunJsonPayload(),
            message = message,
            status = status,
        )
    }

    /**
     * Sample payload
     */
    fun sampleWorkflowRunPayload() = workflowRunPayload(
        action = WorkflowRunAction.requested,
        runNumber = 1,
        headBranch = "main",
        repoName = "my-repo",
        owner = "my-owner",
        sender = "my-sender",
        commit = "01234567890",
    )

    /**
     * Sample payload body
     */
    fun sampleWorkflowRunJsonPayload() = sampleWorkflowRunPayload().asJson()

    /**
     * Sample headers for a hook request
     */
    fun payloadHeaders(event: String) = Headers(
        gitHubDelivery = UUID.randomUUID().toString(),
        gitHubEvent = event,
        gitHubHookID = 123456,
        gitHubHookInstallationTargetID = 1234567890,
        gitHubHookInstallationTargetType = "repository",
    )

    /**
     * Headers needed for a hook
     */
    class Headers(
        val gitHubDelivery: String,
        val gitHubEvent: String,
        val gitHubHookID: Int,
        val gitHubHookInstallationTargetID: Int,
        val gitHubHookInstallationTargetType: String,
    )

    /**
     * Payload for signature test
     */
    const val signatureTestBody = "Sample payload"

    /**
     * Payload for signature test
     */
    const val signatureTestSignature = "sha256=55e4e68a6fe14b9fcfb80478d26be2127bbaf04f187c6ac1441e509c990a1e52"

    /**
     * Token for signature test
     */
    const val signatureTestToken = "131413ebe7b82303ab937236e6294c03e8d53cf6"

}