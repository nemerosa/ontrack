package net.nemerosa.ontrack.model.support;

import java.util.Map;
import java.util.function.Function;

public interface SettingsRepository {

    void delete(Class<?> category, String name);

    /**
     * Deletes all fields in a given settings category.
     *
     * @param category Category to delete.
     */
    void deleteAll(Class<?> category);

    Map<String, String> getAllValues(Class<?> category);

    boolean getBoolean(Class<?> category, String name, boolean defaultValue);

    void setBoolean(Class<?> category, String name, boolean value);

    int getInt(Class<?> category, String name, int defaultValue);

    long getLong(Class<?> category, String name, long defaultValue);

    void setInt(Class<?> category, String name, int value);

    void setLong(Class<?> category, String name, long value);

    String getString(Class<?> category, String name, String defaultValue);

    void setString(Class<?> category, String name, String value);

    String getPassword(Class<?> category, String name, String defaultValue, Function<String, String> decryptService);

    void setPassword(Class<?> category, String name, String plain, boolean dontSaveIfBlank, Function<String, String> encryptService);

}
