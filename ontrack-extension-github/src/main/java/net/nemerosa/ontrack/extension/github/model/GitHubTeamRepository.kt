package net.nemerosa.ontrack.extension.github.model

/**
 * Association of a team & a permission for a repository
 *
 * @param repository Repository name
 * @param permission Permission
 */
data class GitHubTeamRepository(
    val repository: String,
    val permission: GitHubRepositoryPermission
)