package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.common.Caches;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.settings.SettingsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CachedSettingsServiceImpl implements CachedSettingsService {

    private final Map<Class<?>, SettingsProvider<?>> settingsProviders;

    @Autowired
    public CachedSettingsServiceImpl(Collection<SettingsProvider<?>> settingsProviders) {
        this.settingsProviders = settingsProviders.stream()
                .collect(Collectors.toMap(
                        SettingsProvider::getSettingsClass,
                        settingsProvider -> settingsProvider
                ));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable(value = Caches.SETTINGS, key = "#type")
    public <T> T getCachedSettings(Class<T> type) {
        SettingsProvider<T> settingsProvider = (SettingsProvider<T>) settingsProviders.get(type);
        return settingsProvider.getSettings();
    }

    @Override
    @CacheEvict(value = Caches.SETTINGS, key = "#type")
    public <T> void invalidate(Class<T> type) {
    }

}
