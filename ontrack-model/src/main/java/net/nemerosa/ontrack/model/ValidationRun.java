package net.nemerosa.ontrack.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationRun {

    private final String description;
    private final Signature signature;
    private final ValidationStamp validationStamp;
    private final List<ValidationRunStatus> validationRunStatuses;

}
