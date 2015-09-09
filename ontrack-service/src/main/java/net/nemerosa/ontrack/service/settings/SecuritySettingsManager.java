package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecuritySettingsManager extends AbstractSettingsManager<SecuritySettings> {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SecuritySettingsManager(CachedSettingsService cachedSettingsService, SettingsRepository settingsRepository) {
        super(SecuritySettings.class, cachedSettingsService);
        this.settingsRepository = settingsRepository;
    }

    @Override
    protected Form getSettingsForm(SecuritySettings settings) {
        return settings.form();
    }

    @Override
    protected void doSaveSettings(SecuritySettings settings) {
        settingsRepository.setBoolean(SecuritySettings.class, "grantProjectViewToAll", settings.isGrantProjectViewToAll());
    }

}
