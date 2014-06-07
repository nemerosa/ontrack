package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Request data for the creation of a property.
 */
@Data
public class PropertyCreationRequest {

    private final String propertyTypeName;
    private final JsonNode propertyData;

}
