package net.nemerosa.ontrack.extension.github.ingestion.support

/**
 * Owner & name for a repository.
 *
 * @property owner Owner of the repository
 * @property name Name of the repository
 */
data class RepositorySlug(
    val owner: String,
    val name: String,
)
