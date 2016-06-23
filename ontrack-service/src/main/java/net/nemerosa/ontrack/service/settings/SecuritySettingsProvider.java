package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.settings.SettingsProvider;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecuritySettingsProvider implements SettingsProvider<SecuritySettings> {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SecuritySettingsProvider(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public SecuritySettings getSettings() {
        return SecuritySettings.of()
                /**
                 * By default, grants view accesses to everybody.
                 */
                .withGrantProjectViewToAll(settingsRepository.getBoolean(SecuritySettings.class, "grantProjectViewToAll", true));
    }

    @Override
    public Class<SecuritySettings> getSettingsClass() {
        return SecuritySettings.class;
    }
}
