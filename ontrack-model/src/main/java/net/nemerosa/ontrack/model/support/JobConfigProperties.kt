package net.nemerosa.ontrack.model.support

import javax.validation.constraints.Max
import javax.validation.constraints.Min

class JobConfigProperties {
    /**
     * Core pool size for the threads used to run the jobs.
     */
    @Min(1)
    var poolSize = 10
    /**
     * Orchestration interval
     */
    @Min(1)
    var orchestration = 2
    /**
     * Pausing the jobs at startup?
     */
    var pausedAtStartup = false
    /**
     * Using scattering of jobs
     */
    var scattering = true
    /**
     * Scattering ratio (must be between 0.0 and 1.0 inclusive).
     */
    @Min(1)
    @Max(1)
    var scatteringRatio = 1.0
}