package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.settings.GeneralSettings;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeneralSettingsManager extends AbstractSettingsManager<GeneralSettings> {

    private final SettingsRepository settingsRepository;

    @Autowired
    public GeneralSettingsManager(CachedSettingsService cachedSettingsService, SettingsRepository settingsRepository, SecurityService securityService) {
        super(GeneralSettings.class, cachedSettingsService, securityService);
        this.settingsRepository = settingsRepository;
    }

    @Override
    protected Form getSettingsForm(GeneralSettings settings) {
        return settings.form();
    }

    @Override
    protected void doSaveSettings(GeneralSettings settings) {
        settingsRepository.setInt(GeneralSettings.class, "disablingDuration", settings.getDisablingDuration());
        settingsRepository.setInt(GeneralSettings.class, "deletingDuration", settings.getDeletingDuration());
    }

    @Override
    public String getId() {
        return "general-settings";
    }

    @Override
    public String getTitle() {
        return "General settings";
    }
}
