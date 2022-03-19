package net.nemerosa.ontrack.extension.casc

import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.Schedule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CascReloadingJobTest {

    @Test
    fun `Not creating the job if CasC is not enabled`() {
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = false
                reloading.enabled = true
            },
            mockk()
        )
        val jobs = jobProvider.startingJobs
        assertTrue(jobs.isEmpty(), "No job created")
    }

    @Test
    fun `Not creating the job if CasC reloading is not enabled`() {
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = true
                reloading.enabled = false
            },
            mockk()
        )
        val jobs = jobProvider.startingJobs
        assertTrue(jobs.isEmpty(), "No job created")
    }

    @Test
    fun `Creating the job if CasC and Casc reloading are enabled`() {
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = true
                reloading.enabled = true
            },
            mockk()
        )
        val jobs = jobProvider.startingJobs
        assertEquals(1, jobs.size)
    }

    @Test
    fun `Manual job is no cron is specified`() {
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = true
                reloading.enabled = true
            },
            mockk()
        )
        val job = jobProvider.startingJobs.first()
        assertEquals(Schedule.NONE, job.schedule, "Manual job by default")
    }

    @Test
    fun `Cron schedule`() {
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = true
                reloading.enabled = true
                reloading.cron = "@midnight"
            },
            mockk()
        )
        val job = jobProvider.startingJobs.first()
        assertEquals(Schedule.cron("@midnight"), job.schedule, "Cron job")
    }

    @Test
    fun `The job reloads the CasC`() {
        val service = mockk<CascLoadingService>(relaxed = true)
        val jobProvider = CascReloadingJob(
            CascConfigurationProperties().apply {
                enabled = true
                reloading.enabled = true
                reloading.cron = "@midnight"
            },
            service
        )
        val job = jobProvider.startingJobs.first()
        job.job.task.run(JobRunListener.out())
        verify {
            service.load()
        }
    }

}