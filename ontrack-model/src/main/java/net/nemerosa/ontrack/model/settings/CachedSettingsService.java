package net.nemerosa.ontrack.model.settings;

public interface CachedSettingsService {

    <T> T getCachedSettings(Class<T> type);

}
