package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Association between a configuration service ID and an actual configuration data.
 */
@Data
public class ServiceConfiguration {

    private final String id;
    private final JsonNode data;

    public static ServiceConfiguration of(JsonNode node) {
        return new ServiceConfiguration(
                node.get("id").asText(),
                node.get("data")
        );
    }
}
