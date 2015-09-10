package net.nemerosa.ontrack.model.support;

import java.util.function.Function;

public interface SettingsRepository {

    boolean getBoolean(Class<?> category, String name, boolean defaultValue);

    void setBoolean(Class<?> category, String name, boolean value);

    String getString(Class<?> category, String name, String defaultValue);

    void setString(Class<?> category, String name, String value);

    String getPassword(Class<?> category, String name, String defaultValue, Function<String, String> decryptService);

    void setPassword(Class<?> category, String name, String plain, boolean dontSaveIfBlank, Function<String, String> encryptService);

}
