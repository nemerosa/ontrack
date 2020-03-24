package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class ValidationRunStatusChangeRequest {

    private final String validationRunStatusId;
    private final String description;

}
