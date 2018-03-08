package net.nemerosa.ontrack.extension.api

/**
 * Provides some caches which can be configured.
 */
interface CacheConfigExtension {
    /**
     * List of configurable caches.
     *
     * Returns a map whose key is the cache name, and the value is a
     * `com.github.benmanes.caffeine.cache.CaffeineSpec` string specification.
     */
    val caches: Map<String, String>
}