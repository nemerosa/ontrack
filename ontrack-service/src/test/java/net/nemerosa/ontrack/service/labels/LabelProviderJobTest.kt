package net.nemerosa.ontrack.service.labels

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.service.security.SecurityServiceImpl
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LabelProviderJobTest {

    private lateinit var securityService: SecurityService
    private lateinit var structureService: StructureService
    private lateinit var labelProviderService: LabelProviderService
    private lateinit var settingsService: CachedSettingsService

    private lateinit var provider: LabelProviderJob

    private val project1: Project = Project.of(NameDescription.nd("P1", ""))
    private val project2: Project = Project.of(NameDescription.nd("P2", ""))
    private val project3: Project = Project.of(NameDescription.nd("P3", "")).withDisabled(true)
    private val projects = listOf(project1, project2, project3)

    @Before
    fun init() {
        structureService = mock()
        whenever(structureService.projectList).thenReturn(projects)

        labelProviderService = mock()
        settingsService = mock()

        securityService = SecurityServiceImpl()

        provider = LabelProviderJob(
                securityService,
                structureService,
                labelProviderService,
                settingsService
        )
    }

    @Test
    fun `No job when disabled at settings level`() {
        settings(enabled = false, interval = 60, perProject = false)
        val jobs = provider.collectJobRegistrations().toList()
        assertTrue(jobs.isEmpty(), "No job is registered")
    }

    @Test
    fun `Job for all projects`() {
        settings(enabled = true, interval = 180, perProject = false)
        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(1, jobs.size, "Only one job is registered")
        val registration = jobs.first()
        assertEquals(180, registration.schedule.period)
        assertEquals(TimeUnit.MINUTES, registration.schedule.unit)
        val job = registration.job
        job.apply {
            assertEquals(false, job.isDisabled)
            assertEquals("label-collection", job.key.id)
            assertEquals("Collection of automated labels for all projects", job.description)
            task.run { println(it) }
            verify(labelProviderService).collectLabels(project1)
            verify(labelProviderService).collectLabels(project2)
            verify(labelProviderService, never()).collectLabels(project3)
        }
    }

    @Test
    fun `Job per projects`() {
        settings(enabled = true, interval = 240, perProject = true)
        val jobs = provider.collectJobRegistrations().toList()
        assertEquals(3, jobs.size, "One job is registered per project")
        (0..2).forEach { no ->
            val registration = jobs[no]
            assertEquals(240, registration.schedule.period)
            assertEquals(TimeUnit.MINUTES, registration.schedule.unit)
            val job = registration.job
            job.apply {
                assertEquals((no == 2), job.isDisabled)
                assertEquals(projects[no].name, job.key.id)
                assertEquals("Collection of automated labels for project ${projects[no].name}", job.description)
                task.run { println(it) }
                if (no != 2) {
                    verify(labelProviderService).collectLabels(projects[no])
                } else {
                    verify(labelProviderService, never()).collectLabels(projects[no])
                }
            }
        }
    }

    private fun settings(enabled: Boolean, interval: Int, perProject: Boolean) {
        whenever(settingsService.getCachedSettings(LabelProviderJobSettings::class.java)).thenReturn(
                LabelProviderJobSettings(
                        enabled = enabled,
                        interval = interval,
                        perProject = perProject
                )
        )
    }

}