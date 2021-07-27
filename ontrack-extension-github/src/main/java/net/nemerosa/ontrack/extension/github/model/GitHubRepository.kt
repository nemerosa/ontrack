package net.nemerosa.ontrack.extension.github.model

import java.time.LocalDateTime

/**
 * Abstraction for a repository.
 */
class GitHubRepository(
    val name: String,
    val description: String?,
    val lastUpdate: LocalDateTime?,
    val createdAt: LocalDateTime?
)
