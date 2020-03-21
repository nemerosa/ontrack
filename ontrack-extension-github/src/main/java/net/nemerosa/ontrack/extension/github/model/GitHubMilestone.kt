package net.nemerosa.ontrack.extension.github.model

data class GitHubMilestone(
        val title: String,
        val state: GitHubState,
        val number: Int,
        val url: String
)
