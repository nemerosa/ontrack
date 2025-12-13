package net.nemerosa.ontrack.model.settings;

public interface SettingsManager<T> {

    String getId();

    String getTitle();

    T getSettings();

    void saveSettings(T settings);

    Class<T> getSettingsClass();

}
