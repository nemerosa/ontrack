package net.nemerosa.ontrack.extension.indicators.computing

import com.nhaarman.mockitokotlin2.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputer
import net.nemerosa.ontrack.extension.indicators.model.id
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Before
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IndicatorComputingJobsTest {

    private lateinit var structureService: StructureService
    private lateinit var indicatorComputer: IndicatorComputer
    private lateinit var indicatorComputingService: IndicatorComputingService
    private lateinit var meterRegistry: MeterRegistry

    private lateinit var computingJobs: IndicatorComputingJobs

    @Before
    fun before() {
        structureService = mock()
        indicatorComputer = mock()
        indicatorComputingService = mock()
        meterRegistry = SimpleMeterRegistry()

        computingJobs = IndicatorComputingJobs(
                structureService,
                listOf(indicatorComputer),
                indicatorComputingService,
                meterRegistry
        )
    }

    @Test
    fun `Computer job per project`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }
        val project = projects.first()

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(true)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(true)
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(2, jobs.size, "One job created per project")
        val jobRegistration = jobs.first()
        assertEquals(Schedule.EVERY_DAY, jobRegistration.schedule)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals(IndicatorComputingJobs.CATEGORY.key, job.key.type.category.key)
        assertEquals(indicatorComputer.id, job.key.type.key)
        assertEquals(project.name, job.key.id)
        assertTrue(job.description.contains("Computer name"))
        assertTrue(job.description.contains(project.name))

        job.task.run(JobRunListener.out())

        verify(indicatorComputingService, times(1)).compute(
                indicatorComputer,
                project
        )
    }

    @Test
    fun `Computer job per project with one not eligible`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(true)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            // Only first project is eligible
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(
                    it == projects[0]
            )
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(1, jobs.size, "Only one job has been created")
        val jobRegistration = jobs.first()
        assertEquals(Schedule.EVERY_DAY, jobRegistration.schedule)
        val job = jobRegistration.job
        assertEquals(projects[0].name, job.key.id)
    }

    @Test
    fun `Computer job for all projects`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(false)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(true)
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(1, jobs.size, "One job created for all projects")
        val jobRegistration = jobs.first()
        assertEquals(Schedule.EVERY_DAY, jobRegistration.schedule)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals(IndicatorComputingJobs.CATEGORY.key, job.key.type.category.key)
        assertEquals(indicatorComputer.id, job.key.type.key)
        assertEquals("all", job.key.id)
        assertTrue(job.description.contains("Computer name"))
        assertTrue(job.description.contains("all projects", ignoreCase = true))

        job.task.run(JobRunListener.out())

        projects.forEach { project ->
            verify(indicatorComputingService, times(1)).compute(
                    indicatorComputer,
                    project
            )
        }
    }

    @Test
    fun `Computer job for all projects with one being not eligible`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(false)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(
                    // Only first is eligible
                    it == projects[0]
            )
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(1, jobs.size, "One job created for all projects")
        val jobRegistration = jobs.first()
        assertEquals(Schedule.EVERY_DAY, jobRegistration.schedule)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals(IndicatorComputingJobs.CATEGORY.key, job.key.type.category.key)
        assertEquals(indicatorComputer.id, job.key.type.key)
        assertEquals("all", job.key.id)
        assertTrue(job.description.contains("Computer name"))
        assertTrue(job.description.contains("all projects", ignoreCase = true))

        job.task.run(JobRunListener.out())

        projects.forEach { project ->
            val mode = if (project == projects[0]) {
                times(1)
            } else {
                never()
            }
            verify(indicatorComputingService, mode).compute(
                    indicatorComputer,
                    project
            )
        }
    }

    @Test
    fun `Computer job for all projects with one being disabled`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
                    .withDisabled(it == 2)
        }

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(false)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(true)
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(1, jobs.size, "One job created for all projects")
        val jobRegistration = jobs.first()
        assertEquals(Schedule.EVERY_DAY, jobRegistration.schedule)
        val job = jobRegistration.job
        assertFalse(job.isDisabled)
        assertEquals(IndicatorComputingJobs.CATEGORY.key, job.key.type.category.key)
        assertEquals(indicatorComputer.id, job.key.type.key)
        assertEquals("all", job.key.id)
        assertTrue(job.description.contains("Computer name"))
        assertTrue(job.description.contains("all projects", ignoreCase = true))

        job.task.run(JobRunListener.out())

        projects.forEach { project ->
            val mode = if (project == projects[0]) {
                times(1)
            } else {
                never()
            }
            verify(indicatorComputingService, mode).compute(
                    indicatorComputer,
                    project
            )
        }
    }

    @Test
    fun `Computer job per  disabled project`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
                    .withDisabled(it == 1)
        }
        val project = projects.first()
        assertTrue(project.isDisabled)

        whenever(structureService.projectList).thenReturn(projects)

        whenever(indicatorComputer.perProject).thenReturn(true)
        whenever(indicatorComputer.schedule).thenReturn(Schedule.EVERY_DAY)
        whenever(indicatorComputer.name).thenReturn("Computer name")
        projects.forEach {
            whenever(indicatorComputer.isProjectEligible(it)).thenReturn(true)
        }

        val jobs = computingJobs.collectJobRegistrations().toList()

        assertEquals(2, jobs.size, "One job created per project")
        assertTrue(jobs[0].job.isDisabled, "Job disabled because project is")
        assertFalse(jobs[1].job.isDisabled, "Job not disabled because project is not")
    }

}