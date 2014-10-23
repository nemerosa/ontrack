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

}
