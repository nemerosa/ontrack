package net.nemerosa.ontrack.model.support;

import lombok.Data;
import org.apache.commons.lang3.Validate;

@Data
public class JobConfigProperties {

    /**
     * Core pool size for the threads used to run the jobs.
     */
    private int poolSize = 10;

    /**
     * Orchestration interval
     */
    private int orchestration = 2;

    /**
     * Pausing the jobs at startup?
     */
    private boolean pausedAtStartup = false;

    /**
     * Using scattering of jobs
     */
    private boolean scattering = false;

    /**
     * Scattering ratio (must be between 0.0 and 1.0 inclusive).
     */
    private double scatteringRatio = 1.0;

    /**
     * Sets the scattering ratio (must be between 0.0 and 1.0 inclusive).
     */
    @SuppressWarnings("unused")
    public void setScatteringRatio(double value) {
        Validate.inclusiveBetween(0.0, 1.0, value);
        this.scatteringRatio = value;
    }

}
