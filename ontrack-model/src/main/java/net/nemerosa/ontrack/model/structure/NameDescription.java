package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class NameDescription {

    /**
     * Regular expression to validate a name.
     */
    public static final String NAME = "[A-Za-z0-9\\.-_]+";

    private final String name;
    private final String description;

}
