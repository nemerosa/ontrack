package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.form.Form;

public interface SettingsManager<T> {

    String getTitle();

    Form getSettingsForm();

    T getSettings();

    void saveSettings(T settings);

    Class<T> getSettingsClass();

}
