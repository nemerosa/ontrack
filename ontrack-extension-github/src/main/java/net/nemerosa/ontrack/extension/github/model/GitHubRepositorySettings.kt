package net.nemerosa.ontrack.extension.github.model

class GitHubRepositorySettings(
    val hasWikiEnabled: Boolean,
    val hasIssuesEnabled: Boolean,
    val hasProjectsEnabled: Boolean,
    val visibility: GitHubRepositoryVisibility?,
    val defaultBranch: String?,
    val description: String?,
)
