package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;

public abstract class AbstractSettingsManager<T> implements SettingsManager<T> {

    private final Class<T> settingsClass;
    private final CachedSettingsService cachedSettingsService;
    private final SecurityService securityService;

    protected AbstractSettingsManager(Class<T> settingsClass, CachedSettingsService cachedSettingsService, SecurityService securityService) {
        this.settingsClass = settingsClass;
        this.cachedSettingsService = cachedSettingsService;
        this.securityService = securityService;
    }

    @Override
    public final T getSettings() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return cachedSettingsService.getCachedSettings(settingsClass);
    }

    @Override
    public final Form getSettingsForm() {
        return getSettingsForm(cachedSettingsService.getCachedSettings(settingsClass));
    }

    @Override
    public final void saveSettings(T settings) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        cachedSettingsService.invalidate(settingsClass);
        doSaveSettings(settings);
    }

    protected abstract void doSaveSettings(T settings);

    /**
     * @deprecated Will be removed in V5. Settings form are managed in Next UI.
     */
    @Deprecated
    protected abstract Form getSettingsForm(T settings);

    @Override
    public final Class<T> getSettingsClass() {
        return settingsClass;
    }
}
