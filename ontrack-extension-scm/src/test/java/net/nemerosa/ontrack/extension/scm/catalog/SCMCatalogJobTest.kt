package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SCMCatalogJobTest {

    @Test
    fun `SCM catalog collection job`() {
        val scmCatalog: SCMCatalog = mock()
        val collectorJob = SCMCatalogJob(
                scmCatalog = scmCatalog
        )
        val jobs = collectorJob.startingJobs
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 1)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals("scm", job.key.type.category.key)
        assertEquals("catalog", job.key.type.key)
        assertEquals("collection", job.key.id)
        assertEquals("Collection of SCM Catalog", job.description)

        job.task.run { println(it) }
        verify(scmCatalog).collectSCMCatalog(any())
    }

}