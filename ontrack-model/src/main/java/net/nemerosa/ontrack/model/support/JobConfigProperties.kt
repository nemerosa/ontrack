package net.nemerosa.ontrack.model.support

import javax.validation.constraints.Max
import javax.validation.constraints.Min

class JobConfigProperties {
    /**
     * Core pool size for the threads used to run the jobs.
     */
    @Min(1)
    private var poolSize = 10
    /**
     * Orchestration interval
     */
    @Min(1)
    private var orchestration = 2
    /**
     * Pausing the jobs at startup?
     */
    private var pausedAtStartup = false
    /**
     * Using scattering of jobs
     */
    private var scattering = true
    /**
     * Scattering ratio (must be between 0.0 and 1.0 inclusive).
     */
    @Min(1)
    @Max(1)
    private var scatteringRatio = 1.0
}