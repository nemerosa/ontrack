package net.nemerosa.ontrack.extension.git.model

/**
 * Representation of a pull request, independently from the Git application.
 *
 * @property id Numeric ID of the pull request
 * @property key Display version of the ID of the pull request
 * @property source Source branch of the pull request (Git complete ref, like `refs/heads/master`)
 * @property target Target branch of the pull request (Git complete ref, like `refs/heads/master`)
 * @property title Title of the pull request
 */
class GitPullRequest(
        val id: Int,
        val key: String,
        val source: String,
        val target: String,
        val title: String
)
