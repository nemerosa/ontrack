package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class ValidationRunStatus {

    private final Signature author;
    private final ValidationRunStatusID statusID;
    private final String description;

}
