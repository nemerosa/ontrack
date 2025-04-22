package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.json.asJson
import java.time.LocalDateTime
import java.util.*

object IngestionHookFixtures {

    /**
     * Run name
     */
    const val sampleRunName = "CI"

    /**
     * Workflow run payload
     */
    fun workflowRunPayload(
        runId: Long = 1,
        runName: String = sampleRunName,
        action: WorkflowRunAction,
        runNumber: Int,
        headBranch: String,
        createdAtDate: LocalDateTime = Time.now(),
        repoName: String,
        repoDescription: String = "Repository $repoName",
        owner: String,
        sender: String,
        commit: String,
        event: String = "push",
        htmlUrl: String = "https://github.com/nemerosa/github-ingestion-poc/actions/runs/1395528922",
        pullRequest: WorkflowRunPullRequest? = null,
    ) = WorkflowRunPayload(
        action = action,
        workflowRun = WorkflowRun(
            id = runId,
            name = runName,
            runNumber = runNumber,
            pullRequests = listOfNotNull(pullRequest),
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
            updatedAtDate = null,
            htmlUrl = htmlUrl,
            event = event,
            status = WorkflowJobStepStatus.in_progress,
            conclusion = null,
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
        headCommit = commits.firstOrNull(),
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
     * Sample PR for a workflow run
     */
    fun sampleWorkflowRunPR(
        number: Int = 1,
        repoName: String,
        owner: String = sampleOwner,
    ) = WorkflowRunPullRequest(
        number = number,
        head = WorkflowRunPullRequestBranch(
            ref = "feature/pr",
            repo = WorkflowRunPullRequestBranchRepo(
                name = repoName,
                url = "https://api.github.com/repos/$owner/$repoName",
            )
        ),
        base = WorkflowRunPullRequestBranch(
            ref = "main",
            repo = WorkflowRunPullRequestBranchRepo(
                name = repoName,
                url = "https://api.github.com/repos/$owner/$repoName",
            )
        ),
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
            accountName = "admin",
        )
    }

    /**
     * Sample payload
     */
    fun sampleWorkflowRunPayload(
        runId: Long = 1,
        runName: String = sampleRunName,
        runNumber: Int = 1,
        repoName: String = sampleRepository,
        headBranch: String = sampleBranch,
        event: String = "push",
        pullRequest: WorkflowRunPullRequest? = null,
    ) = workflowRunPayload(
        runId = runId,
        action = WorkflowRunAction.requested,
        runNumber = runNumber,
        runName = runName,
        headBranch = headBranch,
        repoName = repoName,
        owner = sampleOwner,
        sender = "my-sender",
        commit = "01234567890",
        event = event,
        pullRequest = pullRequest,
    )

    /**
     * Sample push payload
     */
    fun samplePushPayload(
        id: String = "01234567ef",
        repoName: String = sampleRepository,
        owner: String = sampleOwner,
        ref: String = "refs/heads/$sampleBranch",
        added: List<String> = emptyList(),
        modified: List<String> = emptyList(),
        removed: List<String> = emptyList(),
    ) = pushPayload(
        ref = ref,
        repoName = repoName,
        owner = owner,
        commits = listOf(
            sampleCommit(
                id = id,
                added = added,
                removed = removed,
                modified = modified,
            )
        ),
    )

    fun sampleCommit(
        id: String = "01234567ef",
        added: List<String> = emptyList(),
        modified: List<String> = emptyList(),
        removed: List<String> = emptyList(),
    ) = Commit(
        id = id,
        message = "Sample commit",
        author = sampleAuthor(),
        added = added,
        removed = removed,
        modified = modified,
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
    fun sampleWorkflowRunJsonPayload(
        repoName: String = sampleRepository,
    ) = sampleWorkflowRunPayload(
        repoName = repoName,
    ).asJson()

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

    fun sampleIngestionConfig() = IngestionConfig()

    /**
     * Sample pull request
     */
    fun samplePullRequest(
        number: Int = 1,
    ) = PullRequest(
        number = number,
        state = PullRequestState.open,
        head = Branch(
            ref = "refs/heads/main",
            sha = "sha-main",
            repo = BranchRepo(
                name = "repository",
                owner = Owner(
                    login = "nemerosa"
                )
            )
        ),
        base = Branch(
            ref = "refs/heads/feature/branch",
            sha = "sha-feature",
            repo = BranchRepo(
                name = "repository",
                owner = Owner(
                    login = "nemerosa"
                )
            )
        ),
        merged = false,
        mergeable = true,
    )

    /**
     * Sample workflow job payload
     */
    fun sampleWorkflowJobPayload(
        jobName: String = "the-job",
        runId: Long = 1,
    ) = WorkflowJobPayload(
        action = WorkflowJobAction.in_progress,
        workflowJob = WorkflowJob(
            runId = runId,
            runAttempt = 1,
            status = WorkflowJobStepStatus.in_progress,
            conclusion = null,
            startedAtDate = Time.now(),
            completedAtDate = null,
            name = jobName,
            steps = emptyList(),
            htmlUrl = ""
        ),
        repository = sampleRepository()
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