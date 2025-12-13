package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.common.Caches
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CachedSettingsServiceImpl(
    settingsProviders: Collection<SettingsProvider<*>>
) : CachedSettingsService {

    private val settingsProviders: Map<Class<*>, SettingsProvider<*>> =
        settingsProviders.associateBy { it.settingsClass }

    @Cacheable(value = [Caches.SETTINGS], key = "#type")
    override fun <T> getCachedSettings(type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        val settingsProvider = settingsProviders[type] as SettingsProvider<T>
        return settingsProvider.settings
    }

    @CacheEvict(value = [Caches.SETTINGS], key = "#type")
    override fun <T> invalidate(type: Class<T>) {
    }
}
