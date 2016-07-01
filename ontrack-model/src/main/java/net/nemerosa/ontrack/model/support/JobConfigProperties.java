package net.nemerosa.ontrack.model.support;

import lombok.Data;

@Data
public class JobConfigProperties {

    /**
     * Core pool size for the threads used to run the jobs.
     */
    private int poolSize = 10;

    /**
     * Interval (in minutes) between each refresh of the job list.
     */
    private int refresh = 1;

    /**
     * Orchestration interval
     */
    private int orchestration = 10;

}
