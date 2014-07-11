package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;

public interface PreferencesType<T> {

    T fromStorage(JsonNode node);

    JsonNode forStorage(T value);

}
