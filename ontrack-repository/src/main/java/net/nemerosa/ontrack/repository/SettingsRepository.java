package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.settings.LDAPSettings;

public interface SettingsRepository {

    boolean getBoolean(Class<?> category, String name, boolean defaultValue);

    void setBoolean(Class<?> category, String name, boolean value);

    String getString(Class<?> category, String name, String defaultValue);

    void setString(Class<?> category, String name, String value);

    String getPassword(Class<?> category, String name, String defaultValue);

    void setPassword(Class<?> category, String name, String plain, boolean dontSaveIfBlank);

}
