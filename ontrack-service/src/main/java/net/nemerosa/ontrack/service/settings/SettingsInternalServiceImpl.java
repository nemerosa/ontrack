package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.settings.LDAPSettings;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.repository.SettingsRepository;
import net.nemerosa.ontrack.service.Caches;
import net.nemerosa.ontrack.service.support.SettingsInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsInternalServiceImpl implements SettingsInternalService {

    private final SettingsRepository settingsRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public SettingsInternalServiceImpl(SettingsRepository settingsRepository, EncryptionService encryptionService) {
        this.settingsRepository = settingsRepository;
        this.encryptionService = encryptionService;
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

    @Override
    @Cacheable(value = Caches.LDAP_SETTINGS, key = "0")
    public LDAPSettings getLDAPSettings() {
        return new LDAPSettings(
                settingsRepository.getBoolean(LDAPSettings.class, "enabled", false),
                settingsRepository.getString(LDAPSettings.class, "url", ""),
                settingsRepository.getString(LDAPSettings.class, "searchBase", ""),
                settingsRepository.getString(LDAPSettings.class, "searchFilter", ""),
                settingsRepository.getString(LDAPSettings.class, "user", ""),
                settingsRepository.getPassword(LDAPSettings.class, "password", "", encryptionService::decrypt),
                settingsRepository.getString(LDAPSettings.class, "fullNameAttribute", ""),
                settingsRepository.getString(LDAPSettings.class, "emailAttribute", "")
        );
    }

    @Override
    @CacheEvict(value = Caches.LDAP_SETTINGS, allEntries = true)
    public void saveLDAPSettings(LDAPSettings ldapSettings) {
        settingsRepository.setBoolean(LDAPSettings.class, "enabled", ldapSettings.isEnabled());
        settingsRepository.setString(LDAPSettings.class, "url", ldapSettings.getUrl());
        settingsRepository.setString(LDAPSettings.class, "searchBase", ldapSettings.getSearchBase());
        settingsRepository.setString(LDAPSettings.class, "searchFilter", ldapSettings.getSearchFilter());
        settingsRepository.setString(LDAPSettings.class, "user", ldapSettings.getUser());
        settingsRepository.setPassword(LDAPSettings.class, "password", ldapSettings.getPassword(), true, encryptionService::encrypt);
        settingsRepository.setString(LDAPSettings.class, "fullNameAttribute", ldapSettings.getFullNameAttribute());
        settingsRepository.setString(LDAPSettings.class, "emailAttribute", ldapSettings.getEmailAttribute());
    }
}
