package net.nemerosa.ontrack.model.settings;

public interface SettingsService {

    SecuritySettings getSecuritySettings();

    void saveSecuritySettings(SecuritySettings securitySettings);

    LDAPSettings getLDAPSettings();

    void saveLDAPSettings(LDAPSettings ldapSettings);
}
