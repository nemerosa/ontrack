package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.settings.GeneralSettings;
import net.nemerosa.ontrack.model.settings.SettingsProvider;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeneralSettingsProvider implements SettingsProvider<GeneralSettings> {

    private final SettingsRepository settingsRepository;

    @Autowired
    public GeneralSettingsProvider(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public GeneralSettings getSettings() {
        return GeneralSettings.of()
                .withDisablingDuration(settingsRepository.getInt(GeneralSettings.class, "disablingDuration", 0))
                .withDeletingDuration(settingsRepository.getInt(GeneralSettings.class, "deletingDuration", 0))
                ;
    }

    @Override
    public Class<GeneralSettings> getSettingsClass() {
        return GeneralSettings.class;
    }
}
