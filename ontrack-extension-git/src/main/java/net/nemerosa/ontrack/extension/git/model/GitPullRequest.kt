package net.nemerosa.ontrack.extension.git.model

/**
 * Representation of a pull request, independently from the Git application.
 *
 * @property id Numeric ID of the pull request
 * @property key Display version of the ID of the pull request
 * @property source Source branch of the pull request (Git complete ref, like `refs/heads/master`)
 * @property target Target branch of the pull request (Git complete ref, like `refs/heads/master`)
 * @property title Title of the pull request
 * @property url URL to get more details about the pull request
 */
class GitPullRequest private constructor(
        val id: Int,
        val isValid: Boolean,
        val key: String,
        val source: String,
        val target: String,
        val title: String,
        val status: String,
        val url: String
) {
    fun simplifyBranchNames() = GitPullRequest(
            id = id,
            isValid = isValid,
            key = key,
            source = simpleBranchName(source),
            target = simpleBranchName(target),
            title = title,
            status = status,
            url = url
    )

    constructor(id: Int, key: String, source: String, target: String, title: String, status: String, url: String) : this(
            id = id,
            isValid = true,
            key = key,
            source = source,
            target = target,
            title = title,
            status = status,
            url = url
    )

    companion object {

        fun simpleBranchName(ref: String) = ref.removePrefix("refs/heads/")

        fun invalidPR(id: Int, key: String) = GitPullRequest(
                id = id,
                isValid = false,
                key = key,
                source = "",
                target = "",
                title = "",
                status = "",
                url = ""
        )

    }

}