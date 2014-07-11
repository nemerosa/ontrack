package net.nemerosa.ontrack.model.structure;

public interface PreferencesService {

    <T> T load(PreferencesType<T> preferencesType, T defaultValue);

    <T> void store(PreferencesType<T> preferencesType, T value);

}
