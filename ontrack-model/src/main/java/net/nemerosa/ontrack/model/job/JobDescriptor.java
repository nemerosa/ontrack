package net.nemerosa.ontrack.model.job;

import lombok.Data;

@Data
public class JobDescriptor {

    private final String group;
    private final String category;
    private final String id;
    private final String description;
    private final boolean disabled;
    private final int interval;

}
