package net.nemerosa.ontrack.service

import com.github.benmanes.caffeine.cache.Caffeine
import net.nemerosa.ontrack.common.Caches
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig(
        val cacheConfigProperties: CacheConfigProperties
) {
    @Bean
    fun cacheManager(): CacheManager {
        val manager = SimpleCacheManager()

        manager.setCaches(
                listOf(
                        // Cache for settings
                        CaffeineCache(
                                Caches.SETTINGS,
                                Caffeine.newBuilder()
                                        .maximumSize(1)
                                        .expireAfterWrite(10, TimeUnit.HOURS)
                                        .build()
                        ),
                        // Cache for properties
                        toCache(
                                "properties",
                                "maximumSize=1000,expireAfterWrite=1d"
                        )
                )
        )

        return manager
    }

    private fun toCache(name: String, defaultSpec: String) = CaffeineCache(
            name,
            Caffeine.from(
                    cacheConfigProperties.specs[name] ?: defaultSpec
            ).build()
    )

}