package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Branch
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.BranchRepo
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequestState

object PRPayloadFixtures {

    fun payload(
        action: PRPayloadAction,
        repoName: String = IngestionHookFixtures.sampleRepository,
        number: Int = 1,
        state: PullRequestState = PullRequestState.open,
        merged: Boolean = false,
        mergeable: Boolean = true,
    ) = PRPayload(
        repository = IngestionHookFixtures.sampleRepository(
            repoName = repoName,
        ),
        action = action,
        pullRequest = PullRequest(
            number = number,
            state = state,
            head = Branch(
                ref = "refs/heads/feature/sample-pr",
                repo = BranchRepo(
                    name = repoName,
                ),
            ),
            base = Branch(
                ref = "refs/heads/main",
                repo = BranchRepo(
                    name = repoName,
                ),
            ),
            merged = merged,
            mergeable = mergeable,
        )
    )

}