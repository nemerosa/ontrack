package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.GitChangeLog

/**
 * Service used to access the cache for the change logs, used by the Rest controller
 * _and_ the GraphQL root queries.
 */
interface GitChangeLogCache {

    /**
     * Puts the given change log into the cache.
     */
    fun put(changeLog: GitChangeLog)

    /**
     * Gets a required cache entry using its UUID or throws an exception.
     */
    fun getRequired(uuid: String): GitChangeLog

}

/**
 * Cache section
 */
const val CACHE_GIT_CHANGE_LOG = "gitChangeLog"