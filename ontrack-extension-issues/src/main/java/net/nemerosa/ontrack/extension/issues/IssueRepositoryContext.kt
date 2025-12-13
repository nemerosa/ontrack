package net.nemerosa.ontrack.extension.issues

/**
 * This data is used to give some functions of the [IssueServiceExtension] some context
 * about the SCM repository they are called for.
 *
 * @property repositoryType Type of repository as known by Yontrack: github, stash, etc.
 * @property repositoryName Name of the repository (not its URL)
 */
data class IssueRepositoryContext(
    val repositoryType: String,
    val repositoryName: String,
)
