package net.nemerosa.ontrack.model.settings;

@Deprecated
public interface SettingsService {

    SecuritySettings getSecuritySettings();

    void saveSecuritySettings(SecuritySettings securitySettings);
}
