package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

/**
 * Settings for collecting SCM Catalog from GitHub (and GitHub in general).
 *
 * @property orgs List of organizations the catalog must collect information about.
 * @property autoMergeTimeout Number of milliseconds to wait for an auto merge to be done
 * @property autoMergeInterval Number of milliseconds to wait between each auto merge control
 */
data class GitHubSCMCatalogSettings(
    val orgs: List<String>,
    @APILabel("Auto merge timeout")
    @APIDescription("Number of milliseconds to wait for an auto merge to be done")
    val autoMergeTimeout: Long,
    @APILabel("Auto merge interval")
    @APIDescription("Number of milliseconds to wait between each auto merge control")
    val autoMergeInterval: Long,
) {
    companion object {
        val DEFAULT_AUTO_MERGE_TIMEOUT = Duration.ofMinutes(10).toMillis()
        val DEFAULT_AUTO_MERGE_INTERVAL = Duration.ofSeconds(30).toMillis()
    }
}
