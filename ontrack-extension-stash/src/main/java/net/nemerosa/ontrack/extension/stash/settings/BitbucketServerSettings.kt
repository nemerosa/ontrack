package net.nemerosa.ontrack.extension.stash.settings

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

/**
 * General settings for Bitbucket Server.
 *
 * @property autoMergeTimeout Number of milliseconds to wait for an auto merge to be done
 * @property autoMergeInterval Number of milliseconds to wait between each auto merge control
 */
data class BitbucketServerSettings(
    @APILabel("Auto merge timeout")
    @APIDescription("Number of milliseconds to wait for an auto merge to be done")
    val autoMergeTimeout: Long = DEFAULT_AUTO_MERGE_TIMEOUT,
    @APILabel("Auto merge interval")
    @APIDescription("Number of milliseconds to wait between each auto merge control")
    val autoMergeInterval: Long = DEFAULT_AUTO_MERGE_INTERVAL,
    @APILabel("Maximum number of commits to return for a change log")
    val maxCommits: Int = DEFAULT_MAX_COMMITS,
    @APIDescription("Deleting the source branch when an auto-versioning PR is merged")
    val autoDeleteBranch: Boolean = DEFAULT_AUTO_DELETE_BRANCH,
) {
    companion object {
        val DEFAULT_AUTO_MERGE_TIMEOUT = Duration.ofMinutes(10).toMillis()
        val DEFAULT_AUTO_MERGE_INTERVAL = Duration.ofSeconds(30).toMillis()
        const val DEFAULT_MAX_COMMITS: Int = 1000
        const val DEFAULT_AUTO_DELETE_BRANCH: Boolean = true
    }
}
