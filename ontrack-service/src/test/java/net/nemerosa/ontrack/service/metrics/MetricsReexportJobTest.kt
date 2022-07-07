package net.nemerosa.ontrack.service.metrics

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.metrics.MetricsReexportJobProvider
import org.junit.jupiter.api.Test

internal class MetricsReexportJobTest {

    @Test
    fun `Reexporting triggers a preparation for the metrics extensions`() {

        val extension = mockk<MetricsExportExtension>(relaxed = true)

        val extensionManager = mockk<ExtensionManager>()
        every { extensionManager.getExtensions(MetricsExportExtension::class.java) } returns listOf(extension)

        val metricsReexportJobProvider = mockk<MetricsReexportJobProvider>(relaxed = true)
        val jobKey = JobCategory.of("test-category").getType("test-type").getKey("test")
        every { metricsReexportJobProvider.getReexportJobKey() } returns jobKey

        val metricsReexportJobProviders = listOf(metricsReexportJobProvider)

        val jobScheduler = mockk<JobScheduler>(relaxed = true)

        val job = MetricsReexportJob(
            extensionManager,
            metricsReexportJobProviders,
            jobScheduler,
        )

        job.task.run(JobRunListener.out())

        verify {
            extension.prepareReexport()
            jobScheduler.fireImmediately(jobKey)
        }

    }

}