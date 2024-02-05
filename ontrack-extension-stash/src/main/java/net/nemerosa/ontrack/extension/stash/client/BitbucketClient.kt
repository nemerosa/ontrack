package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.BitbucketProject
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.scm.BitbucketServerPR
import java.time.LocalDateTime

interface BitbucketClient {

    val projects: List<BitbucketProject>

    fun getRepositories(project: BitbucketProject): List<BitbucketRepository>

    /**
     * Gets the last modified date for a given [repository][repo].
     */
    fun getRepositoryLastModified(repo: BitbucketRepository): LocalDateTime?

    /**
     * Creates a branch into the target repository.
     *
     * @param repo Repository where to create a branch
     * @param source Source branch
     * @param target Name of the branch to create
     */
    fun createBranch(repo: BitbucketRepository, source: String, target: String): String

    /**
     * Downloads the content of a file.
     *
     * @param repo Repository where to find the file
     * @param branch Branch in which to find the file (null for the default branch)
     * @param path Path to file
     * @return Binary content of the file (or null if not found)
     */
    fun download(repo: BitbucketRepository, branch: String?, path: String): ByteArray?

    /**
     * Uploads the content of a file.
     *
     * @param repo Repository where to upload the file
     * @param branch Branch in which to create/update the file
     * @param commit Base commit for the file
     * @param path Path to file
     * @param content Content of the file
     * @param message Commit message
     */
    fun upload(
        repo: BitbucketRepository,
        branch: String,
        commit: String,
        path: String,
        content: ByteArray,
        message: String
    )

    /**
     * Creates a new PR.
     *
     * @param repo Repository where to create the PR
     * @param title Title of the PR
     * @param body Description of the PR
     * @param head Source of the PR
     * @param base Target of the PR
     */
    fun createPR(
        repo: BitbucketRepository,
        title: String,
        head: String,
        base: String,
        body: String,
        reviewers: List<String>,
    ): BitbucketServerPR

    /**
     * Approves a PR.
     *
     * @param repo Repository where the PR is
     * @param prId ID of the PR
     * @param user Slug for the account doing the approval
     * @param token HTTP access token of the account doing the approval
     */
    fun approvePR(repo: BitbucketRepository, prId: Int, user: String, token: String)

    /**
     * Checks if a PR can be merged.
     *
     * @param repo Repository where the PR is
     * @param prId ID of the PR
     * @return True if the PR can be merged
     */
    fun isPRMergeable(repo: BitbucketRepository, prId: Int): Boolean

    /**
     * Merges a PR.
     *
     * @param repo Repository where the PR is
     * @param prId ID of the PR
     * @param message Merge message
     */
    fun mergePR(repo: BitbucketRepository, prId: Int, message: String)

    /**
     * Gets a list of commits between two boundaries.
     */
    fun getCommits(repo: BitbucketRepository, fromCommit: String, toCommit: String): List<BitbucketServerCommit>

}