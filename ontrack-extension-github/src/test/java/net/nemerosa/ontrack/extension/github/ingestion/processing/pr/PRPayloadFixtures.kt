package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*

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
                sha = "any-commit-pr",
                repo = BranchRepo(
                    name = repoName,
                    owner = Owner(login = IngestionHookFixtures.sampleOwner),
                ),
            ),
            base = Branch(
                ref = "refs/heads/main",
                sha = "any-commit-main",
                repo = BranchRepo(
                    name = repoName,
                    owner = Owner(login = IngestionHookFixtures.sampleOwner),
                ),
            ),
            merged = merged,
            mergeable = mergeable,
        )
    )

}