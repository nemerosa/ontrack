package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

@Data
public class ValidationRunRequest {

    private final Integer validationStampId;
    private final String validationStampName;
    private final String validationRunStatusId;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"validationStampId", "validationStampName", "validationRunStatusId", "description", "properties"})
    public ValidationRunRequest(Integer validationStampId, String validationStampName, String validationRunStatusId, String description, List<PropertyCreationRequest> properties) {
        this.validationStampId = validationStampId;
        this.validationStampName = validationStampName;
        this.validationRunStatusId = validationRunStatusId;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

}
