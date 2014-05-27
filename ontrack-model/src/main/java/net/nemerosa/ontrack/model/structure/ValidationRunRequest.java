package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class ValidationRunRequest {

    private final int validationStamp;
    private final String validationRunStatusId;
    private final String description;

}
