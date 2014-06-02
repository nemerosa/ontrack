package net.nemerosa.ontrack.repository;

public interface SettingsRepository {

    boolean getBoolean(Class<?> category, String name, boolean defaultValue);

    void setBoolean(Class<?> category, String name, boolean value);
}
