package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.settings.SettingsService;
import net.nemerosa.ontrack.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SecurityService securityService;
    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SecurityService securityService, SettingsRepository settingsRepository) {
        this.securityService = securityService;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public SecuritySettings getSecuritySettings() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return new SecuritySettings(
                settingsRepository.getBoolean(SecuritySettings.class, "grantProjectViewToAll", false)
        );
    }

    @Override
    public void saveSecuritySettings(SecuritySettings securitySettings) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        settingsRepository.setBoolean(SecuritySettings.class, "grantProjectViewToAll", securitySettings.isGrantProjectViewToAll());
    }

}
