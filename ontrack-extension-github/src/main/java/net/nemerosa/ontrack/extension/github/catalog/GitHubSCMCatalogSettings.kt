package net.nemerosa.ontrack.extension.github.catalog

/**
 * Settings for collecting SCM Catalog from GitHub.
 *
 * @property orgs List of organizations the catalog must collect information about.
 */
data class GitHubSCMCatalogSettings(
        val orgs: List<String>
)
