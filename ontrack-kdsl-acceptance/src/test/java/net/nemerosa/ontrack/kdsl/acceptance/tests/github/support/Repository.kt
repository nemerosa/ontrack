package net.nemerosa.ontrack.kdsl.acceptance.tests.github.support

/**
 * Integration with a real test repository in GitHub
 *
 * @property name Name of the repository
 */
class Repository(
    val name: String,
) {

    /**
     * Creates a branch
     *
     * @param head Name of the new branch
     * @param base Name of the base branch
     */
    fun createBranch(
        head: String,
        base: String = "main",
    ) {
        TODO()
    }

    /**
     * Creates a file on a branch.
     *
     * @param branch Name of the branch
     * @param path Path to the file
     * @param content Content of the file
     */
    fun createFile(
        branch: String,
        path: String,
        content: String,
    ) {
        TODO()
    }

    /**
     * Creates a PR
     *
     * @param head Name of the new branch
     * @param base Name of the base branch
     * @param title Title of the PR
     * @param description Description of the PR
     * @return Created pull request
     */
    fun createPR(
        head: String,
        base: String,
        title: String,
        description: String,
    ): PullRequest {
        TODO()
    }

    /**
     * Merges a PR.
     *
     * @param pr Pull request to merge
     */
    fun mergePR(pr: PullRequest) {
        TODO()
    }

}