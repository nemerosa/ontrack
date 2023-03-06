package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogUUIDException
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Component
class GitChangeLogCacheImpl(
    cacheManager: CacheManager,
) : GitChangeLogCache {

    private val logCache: Cache by lazy {
        cacheManager.getCache(CACHE_GIT_CHANGE_LOG) ?: throw GitChangeLogCacheNotAvailableException()
    }

    override fun put(changeLog: GitChangeLog) {
        logCache.put(changeLog.uuid, changeLog)
    }

    override fun getRequired(uuid: String): GitChangeLog =
        logCache.get(uuid)?.get() as? GitChangeLog?
            ?: throw SCMChangeLogUUIDException(uuid)

}