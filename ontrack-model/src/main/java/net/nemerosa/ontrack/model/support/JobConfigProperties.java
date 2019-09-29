package net.nemerosa.ontrack.model.support;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class JobConfigProperties {

    /**
     * Core pool size for the threads used to run the jobs.
     */
    @Min(1)
    private int poolSize = 10;

    /**
     * Orchestration interval
     */
    @Min(1)
    private int orchestration = 2;

    /**
     * Pausing the jobs at startup?
     */
    private boolean pausedAtStartup = false;

    /**
     * Using scattering of jobs
     */
    private boolean scattering = true;

    /**
     * Scattering ratio (must be between 0.0 and 1.0 inclusive).
     */
    @Min(1)
    @Max(1)
    private double scatteringRatio = 1.0;

}
