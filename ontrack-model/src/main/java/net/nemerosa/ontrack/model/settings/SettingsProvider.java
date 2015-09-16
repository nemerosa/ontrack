package net.nemerosa.ontrack.model.settings;

public interface SettingsProvider<T> {

    T getSettings();

    Class<T> getSettingsClass();

}
