package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfigGeneral
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRun
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunAction
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
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
        repository = sampleRepository(repoName, repoDescription, owner),
        sender = User(login = sender)
    )

    /**
     * Push payload
     */
    fun pushPayload(
        ref: String,
        repoName: String,
        repoDescription: String? = null,
        owner: String,
        commits: List<Commit>,
    ) = PushPayload(
        ref = ref,
        repository = sampleRepository(repoName, repoDescription, owner),
        commits = commits,
    )

    fun sampleRepository(
        repoName: String = sampleRepository,
        repoDescription: String? = null,
        owner: String = sampleOwner,
        htmlUrl: String = "https://github.com/$owner/$repoName",
    ) = Repository(
        name = repoName,
        description = repoDescription,
        owner = Owner(login = owner),
        htmlUrl = htmlUrl,
    )

    /**
     * Sample payload
     */
    fun sampleWorkflowRunIngestionPayload(
        repoName: String = sampleRepository,
        owner: String = sampleOwner,
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
            repository = sampleRepository(repoName = repoName, owner = owner),
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
        headBranch = sampleBranch,
        repoName = sampleRepository,
        owner = sampleOwner,
        sender = "my-sender",
        commit = "01234567890",
    )

    /**
     * Sample push payload
     */
    fun samplePushPayload(
        ref: String = "refs/heads/$sampleBranch",
        added: List<String> = emptyList(),
        modified: List<String> = emptyList(),
        removed: List<String> = emptyList(),
    ) = pushPayload(
        ref = ref,
        repoName = sampleRepository,
        owner = sampleOwner,
        commits = listOf(
            Commit(
                id = "01234567ef",
                message = "Sample commit",
                author = sampleAuthor(),
                added = added,
                removed = removed,
                modified = modified,
            )
        )
    )

    fun sampleAuthor() = Author(
        name = "Sample Author",
        username = "sample-author",
    )

    /**
     * Sample branch
     */
    const val sampleBranch = "main"

    /**
     * Sample owner
     */
    const val sampleOwner = "my-owner"

    /**
     * Sample repository
     */
    const val sampleRepository = "my-repo"

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

    fun sampleIngestionConfig() = IngestionConfig(
        general = IngestionConfigGeneral(
            skipJobs = true,
        )
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