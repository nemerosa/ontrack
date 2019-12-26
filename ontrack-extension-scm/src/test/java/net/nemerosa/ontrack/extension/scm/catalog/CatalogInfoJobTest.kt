package net.nemerosa.ontrack.extension.scm.catalog

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatalogInfoJobTest {

    private lateinit var structureService: StructureService
    private lateinit var catalogLinkService: CatalogLinkService
    private lateinit var catalogInfoCollector: CatalogInfoCollector

    private lateinit var provider: CatalogInfoJob

    private lateinit var project: Project

    @Before
    fun setup() {
        project = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))
        structureService = mock()
        catalogLinkService = mock()
        catalogInfoCollector = mock()
        provider = CatalogInfoJob(
                structureService,
                catalogLinkService,
                catalogInfoCollector
        )
    }

    @Test
    fun `Collection of catalog information`() {
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(CatalogFixtures.entry())

        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 7)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals("scm", job.key.type.category.key)
        assertEquals("scm-catalog-info", job.key.type.key)
        assertEquals(project.name, job.key.id)
        assertEquals("SCM Catalog info collection for ${project.name}", job.description)

        job.task.run { println(it) }
        verify(catalogInfoCollector).collectCatalogInfo(
                eq(project),
                any()
        )
    }

    @Test
    fun `Collection of catalog information is disabled if project is disabled`() {
        project = project.withDisabled(true)
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(CatalogFixtures.entry())

        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 7)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        assertTrue(job.isDisabled, "Job is disabled because project is disabled")
    }

    @Test
    fun `Collection of catalog information is disabled if project is no longer associated to a catalog entry`() {
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(CatalogFixtures.entry())

        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(1, jobs.size)
        val jobRegistration = jobs.first()
        assertEquals(jobRegistration.schedule.initialPeriod, 0)
        assertEquals(jobRegistration.schedule.period, 7)
        assertEquals(jobRegistration.schedule.unit, TimeUnit.DAYS)
        val job = jobRegistration.job
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(null)
        assertTrue(job.isDisabled, "Job is disabled because project is no longer associated with a catalog entry")
    }

    @Test
    fun `No catalog information if project not associated with entry`() {
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(catalogLinkService.getSCMCatalogEntry(project)).thenReturn(null)

        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(0, jobs.size)
    }

}