package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.settings.SettingsProvider;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LDAPSettingsProvider implements SettingsProvider<LDAPSettings> {

    private final SettingsRepository settingsRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public LDAPSettingsProvider(SettingsRepository settingsRepository, EncryptionService encryptionService) {
        this.settingsRepository = settingsRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public LDAPSettings getSettings() {
        return new LDAPSettings(
                settingsRepository.getBoolean(LDAPSettings.class, "enabled", false),
                settingsRepository.getString(LDAPSettings.class, "url", ""),
                settingsRepository.getString(LDAPSettings.class, "searchBase", ""),
                settingsRepository.getString(LDAPSettings.class, "searchFilter", ""),
                settingsRepository.getString(LDAPSettings.class, "user", ""),
                settingsRepository.getPassword(LDAPSettings.class, "password", "", encryptionService::decrypt),
                settingsRepository.getString(LDAPSettings.class, "fullNameAttribute", ""),
                settingsRepository.getString(LDAPSettings.class, "emailAttribute", ""),
                settingsRepository.getString(LDAPSettings.class, "groupAttribute", ""),
                settingsRepository.getString(LDAPSettings.class, "groupFilter", ""),
                settingsRepository.getString(LDAPSettings.class, "groupNameAttribute", "cn"),
                settingsRepository.getString(LDAPSettings.class, "groupSearchBase", ""),
                settingsRepository.getString(LDAPSettings.class, "groupSearchFilter", "(member={0})")
        );
    }

    @Override
    public Class<LDAPSettings> getSettingsClass() {
        return LDAPSettings.class;
    }
}
