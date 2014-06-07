package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

@Data
public class ValidationRunRequest {

    private final int validationStamp;
    private final String validationRunStatusId;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"validationStamp", "validationRunStatusId", "description", "properties"})
    public ValidationRunRequest(int validationStamp, String validationRunStatusId, String description, List<PropertyCreationRequest> properties) {
        this.validationStamp = validationStamp;
        this.validationRunStatusId = validationRunStatusId;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

}
