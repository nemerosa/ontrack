package net.nemerosa.ontrack.extension.scm.catalog.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SCMCatalogMetricsJobTest {

    @Test
    fun `Collection of catalog metrics`() {
        val cache: SCMCatalogMetricsCache = mock()
        val filterService: SCMCatalogFilterService = mock()
        val provider = SCMCatalogMetricsJob(cache, filterService)

        val counts = mapOf(
                SCMCatalogProjectFilterLink.ALL to 10,
                SCMCatalogProjectFilterLink.ENTRY to 8,
                SCMCatalogProjectFilterLink.LINKED to 5,
                SCMCatalogProjectFilterLink.UNLINKED to 3,
                SCMCatalogProjectFilterLink.ORPHAN to 2
        )
        whenever(filterService.indexCatalogProjectEntries()).thenReturn(counts)

        val jobs = provider.startingJobs.toList()
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 1)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals("scm", job.key.type.category.key)
        assertEquals("catalog-metrics", job.key.type.key)
        assertEquals("catalog-metrics", job.key.id)
        assertEquals("Collection of SCM Catalog metrics", job.description)

        job.task.run { println(it) }
        verify(cache).counts = counts
    }
}