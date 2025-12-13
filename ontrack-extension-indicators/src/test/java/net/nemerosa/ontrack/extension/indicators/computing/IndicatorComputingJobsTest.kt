package net.nemerosa.ontrack.extension.indicators.computing

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IndicatorComputingJobsTest {

    private lateinit var structureService: StructureService
    private lateinit var indicatorComputer: IndicatorComputer
    private lateinit var indicatorComputingService: IndicatorComputingService
    private lateinit var meterRegistry: MeterRegistry

    private lateinit var computingJobs: IndicatorComputingJobs

    @BeforeEach
    fun before() {
        structureService = mockk(relaxed = true)
        indicatorComputer = mockk(relaxed = true)
        indicatorComputingService = mockk(relaxed = true)
        meterRegistry = SimpleMeterRegistry()

        computingJobs = IndicatorComputingJobs(
            structureService = structureService,
            computers = listOf(indicatorComputer),
            indicatorComputingService = indicatorComputingService,
            meterRegistry = meterRegistry,
        )
    }

    @Test
    fun `Computer job per project`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }
        val project = projects.first()

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns true
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            every { indicatorComputer.isProjectEligible(it) } returns true
        }

        val jobs = computingJobs.jobRegistrations.toList()

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

        verify(exactly = 1) {
            indicatorComputingService.compute(indicatorComputer, project)
        }
    }

    @Test
    fun `Computer job per project with one not eligible`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns true
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            // Only first project is eligible
            every { indicatorComputer.isProjectEligible(it) } returns (it == projects[0])
        }

        val jobs = computingJobs.jobRegistrations.toList()

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

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns false
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            every { indicatorComputer.isProjectEligible(it) } returns true
        }

        val jobs = computingJobs.jobRegistrations.toList()

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
            verify(exactly = 1) {
                indicatorComputingService.compute(
                    indicatorComputer,
                    project
                )
            }
        }
    }

    @Test
    fun `Computer job for all projects with one being not eligible`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
        }

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns false
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            // Only first is eligible
            every {
                indicatorComputer.isProjectEligible(it)
            } returns (it == projects[0])
        }

        val jobs = computingJobs.jobRegistrations.toList()

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
            if (project == projects[0]) {
                verify(exactly = 1) {
                    indicatorComputingService.compute(indicatorComputer, project)
                }
            } else {
                verify(exactly = 0) {
                    indicatorComputingService.compute(indicatorComputer, project)
                }
            }
        }
    }

    @Test
    fun `Computer job for all projects with one being disabled`() {
        val projects = (1..2).map {
            Project.of(nd("P$it", ""))
                .withDisabled(it == 2)
        }

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns false
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            every { indicatorComputer.isProjectEligible(it) } returns true
        }

        val jobs = computingJobs.jobRegistrations.toList()

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
            if (project == projects[0]) {
                verify(exactly = 1) {
                    indicatorComputingService.compute(indicatorComputer, project)
                }
            } else {
                verify(exactly = 0) {
                    indicatorComputingService.compute(indicatorComputer, project)
                }
            }
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

        every { structureService.projectList } returns projects

        every { indicatorComputer.perProject } returns true
        every { indicatorComputer.schedule } returns Schedule.EVERY_DAY
        every { indicatorComputer.name } returns "Computer name"
        projects.forEach {
            every { indicatorComputer.isProjectEligible(it) } returns true
        }

        val jobs = computingJobs.jobRegistrations.toList()

        assertEquals(2, jobs.size, "One job created per project")
        assertTrue(jobs[0].job.isDisabled, "Job disabled because project is")
        assertFalse(jobs[1].job.isDisabled, "Job not disabled because project is not")
    }

}