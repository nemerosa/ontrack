package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface PreferencesRepository {

    Optional<JsonNode> find(int accountId, String type);

    void store(int accountId, String type, JsonNode data);

}
