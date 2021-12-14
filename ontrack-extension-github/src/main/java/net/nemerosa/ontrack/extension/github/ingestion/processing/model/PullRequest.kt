package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Abstract representation of a pull request, compatible between the full blown representations
 * available in the PR events and the stubs in the workflow runs.
 */
interface IPullRequest {
    /**
     * Number of the PR
     */
    val number: Int
}

@JsonIgnoreProperties(ignoreUnknown = true)
class PullRequest(
    override val number: Int,
    val state: PullRequestState,
    val head: Branch,
    val base: Branch,
    val merged: Boolean,
    val mergeable: Boolean?,
) : IPullRequest {
    fun sameRepo() =
        head.repo.owner.login == base.repo.owner.login &&
                head.repo.name == base.repo.name
}
