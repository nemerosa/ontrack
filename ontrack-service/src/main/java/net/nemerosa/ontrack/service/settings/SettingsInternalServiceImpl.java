package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.repository.SettingsRepository;
import net.nemerosa.ontrack.service.Caches;
import net.nemerosa.ontrack.service.support.SettingsInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SettingsInternalServiceImpl implements SettingsInternalService {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsInternalServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Cacheable(value = Caches.SECURITY_SETTINGS, key = "0")
    public SecuritySettings getSecuritySettings() {
        return SecuritySettings.of()
                .withGrantProjectViewToAll(settingsRepository.getBoolean(SecuritySettings.class, "grantProjectViewToAll", false));
    }

    @Override
    @CacheEvict(value = Caches.SECURITY_SETTINGS, allEntries = true)
    public void saveSecuritySettings(SecuritySettings securitySettings) {
        settingsRepository.setBoolean(SecuritySettings.class, "grantProjectViewToAll", securitySettings.isGrantProjectViewToAll());
    }

}
