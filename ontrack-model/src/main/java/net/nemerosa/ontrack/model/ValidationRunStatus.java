package net.nemerosa.ontrack.model;

import lombok.Data;

@Data
public class ValidationRunStatus {

    private final Signature author;
    private final ValidationRunStatusID statusID;
    private final String description;

}
