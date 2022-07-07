package net.nemerosa.ontrack.service.metrics

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.metrics.MetricsReexportJobProvider
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

/**
 * Job which re-exports all existing metrics.
 */
@Component
class MetricsReexportJob(
    private val extensionManager: ExtensionManager,
    private val metricsReexportJobProviders: List<MetricsReexportJobProvider>,
    private val jobScheduler: JobScheduler,
) : JobProvider {

    override fun getStartingJobs() = listOf(
        JobRegistration(
            createMetricsReexportJob(),
            Schedule.NONE
        )
    )

    private fun createMetricsReexportJob() = object : Job {

        override fun getKey(): JobKey =
            JobCategory.CORE.getType("metrics").withName("Metrics jobs").getKey("restoration")

        override fun getTask() = JobRun { listener ->
            listener.message("Preparing all the metrics extensions...")
            extensionManager.getExtensions(MetricsExportExtension::class.java).forEach { extension ->
                listener.message("Preparing ${extension::class.java.name}...")
                extension.prepareReexport()
            }
            listener.message("Launching all the re-exportations...")
            metricsReexportJobProviders.forEach { metricsReexportJobProvider ->
                val key = metricsReexportJobProvider.getReexportJobKey()
                listener.message("Launching (asynchronously) the re-exportation for $key...")
                jobScheduler.fireImmediately(key).orElse(null)
            }
        }

        override fun getDescription(): String = "Re-export of all metrics"

        override fun isDisabled(): Boolean = false
    }

}