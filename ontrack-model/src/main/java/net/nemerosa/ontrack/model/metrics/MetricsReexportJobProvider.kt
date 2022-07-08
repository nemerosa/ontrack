package net.nemerosa.ontrack.model.metrics

import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.model.support.JobProvider

/**
 * Marker interface for all the [JobProvider] which can re-export metrics.
 */
interface MetricsReexportJobProvider {

    /**
     * Key of the job to launch for the re-export
     */
    fun getReexportJobKey(): JobKey

}