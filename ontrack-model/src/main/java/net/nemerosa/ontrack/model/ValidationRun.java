package net.nemerosa.ontrack.model;

import lombok.Data;

@Data
public class ValidationRun {

    private final String description;
    private final Signature signature;
    private final ValidationStamp validationStamp;

}
