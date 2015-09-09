package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.form.Form;

public abstract class AbstractSettingsManager<T> implements SettingsManager<T> {

    private final Class<T> settingsClass;
    private final CachedSettingsService cachedSettingsService;

    protected AbstractSettingsManager(Class<T> settingsClass, CachedSettingsService cachedSettingsService) {
        this.settingsClass = settingsClass;
        this.cachedSettingsService = cachedSettingsService;
    }

    @Override
    public final T getSettings() {
        return cachedSettingsService.getCachedSettings(settingsClass);
    }

    @Override
    public final Form getSettingsForm() {
        return getSettingsForm(cachedSettingsService.getCachedSettings(settingsClass));
    }

    @Override
    public final void saveSettings(T settings) {
        cachedSettingsService.invalidate(settingsClass);
        doSaveSettings(settings);
    }

    protected abstract void doSaveSettings(T settings);

    protected abstract Form getSettingsForm(T settings);

    @Override
    public final Class<T> getSettingsClass() {
        return settingsClass;
    }
}
