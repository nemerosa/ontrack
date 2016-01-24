package net.nemerosa.ontrack.job;

import lombok.Data;

@Data
public class JobDefinition {

    private final Job job;
    private final Schedule schedule;

}
