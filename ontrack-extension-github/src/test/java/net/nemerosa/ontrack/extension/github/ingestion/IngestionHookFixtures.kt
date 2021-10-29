package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRun
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunAction
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunPayload
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
            name = "CI",
            runNumber = runNumber,
            pullRequests = emptyList(),
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
            htmlUrl = htmlUrl,
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
    fun sampleWorkflowRunIngestionPayload() = payloadHeaders(event = "workflow_run").run {
        IngestionHookPayload(
            gitHubEvent = gitHubEvent,
            gitHubDelivery = gitHubDelivery,
            gitHubHookID = gitHubHookID,
            gitHubHookInstallationTargetID = gitHubHookInstallationTargetID,
            gitHubHookInstallationTargetType = gitHubHookInstallationTargetType,
            payload = sampleWorkflowRunJsonPayload(),
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

}