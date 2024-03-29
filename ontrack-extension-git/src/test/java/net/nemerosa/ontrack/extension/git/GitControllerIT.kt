package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import kotlin.test.assertNotNull

class GitControllerIT : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `The Git change log cache must be provisioned`() {
        val cache = cacheManager.getCache(CACHE_GIT_CHANGE_LOG)
        assertNotNull(cache, "The Git change log cache must be available")
    }

}