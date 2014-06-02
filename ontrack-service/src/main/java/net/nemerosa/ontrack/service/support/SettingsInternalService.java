package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.settings.SecuritySettings;

public interface SettingsInternalService {

    SecuritySettings getSecuritySettings();

    void saveSecuritySettings(SecuritySettings securitySettings);

}
