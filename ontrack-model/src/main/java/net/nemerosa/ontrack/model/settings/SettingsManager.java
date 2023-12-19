package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.form.Form;

public interface SettingsManager<T> {

    String getId();

    String getTitle();

    /**
     * @deprecated Settings form is used only for the legacy V4 UI and will be removed in V5.
     */
    Form getSettingsForm();

    T getSettings();

    void saveSettings(T settings);

    Class<T> getSettingsClass();

}
