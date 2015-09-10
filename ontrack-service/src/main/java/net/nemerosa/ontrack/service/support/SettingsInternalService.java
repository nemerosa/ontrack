package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.settings.LDAPSettings;
import net.nemerosa.ontrack.model.settings.SecuritySettings;

@Deprecated
public interface SettingsInternalService {

    SecuritySettings getSecuritySettings();

    void saveSecuritySettings(SecuritySettings securitySettings);

    LDAPSettings getLDAPSettings();

    void saveLDAPSettings(LDAPSettings ldapSettings);
}
