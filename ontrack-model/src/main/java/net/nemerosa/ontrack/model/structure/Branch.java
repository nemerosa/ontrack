package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class Branch {

    private final String id;
    private final String name;
    private final String description;
    private final Project project;

}
