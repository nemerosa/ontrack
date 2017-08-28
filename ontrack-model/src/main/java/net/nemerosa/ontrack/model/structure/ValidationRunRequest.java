package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

@Data
public class ValidationRunRequest {

    @Deprecated
    private final Integer validationStampId;
    private final ServiceConfiguration validationStampData;
    private final String validationStampName;
    private final String validationRunStatusId;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"validationStampId", "validationStampData", "validationStampName", "validationRunStatusId", "description", "properties"})
    public ValidationRunRequest(Integer validationStampId, ServiceConfiguration validationStampData, String validationStampName, String validationRunStatusId, String description, List<PropertyCreationRequest> properties) {
        this.validationStampId = validationStampId;
        this.validationStampData = validationStampData;
        this.validationStampName = validationStampName;
        this.validationRunStatusId = validationRunStatusId;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

    @JsonIgnore
    public String getActualValidationStampName() {
        if (validationStampData != null) {
            return validationStampData.getId();
        } else {
            return validationStampName;
        }
    }

}
