package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CatalogLinkJobTest {

    @Test
    fun `Collection of catalog links`() {
        val service: CatalogLinkService = mock()
        val provider = CatalogLinkJob(service)

        val jobs = provider.startingJobs.toList()
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 7)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals("scm", job.key.type.category.key)
        assertEquals("catalog-link", job.key.type.key)
        assertEquals("catalog-link", job.key.id)
        assertEquals("Catalog links collection", job.description)

        job.task.run { println(it) }
        verify(service).computeCatalogLinks()
    }

}