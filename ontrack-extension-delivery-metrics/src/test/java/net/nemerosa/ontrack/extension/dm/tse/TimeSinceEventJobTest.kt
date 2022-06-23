package net.nemerosa.ontrack.extension.dm.tse

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.git.branching.BranchingModelPropertyType
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.streams.toList
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit test for [TimeSinceEventJob].
 */
class TimeSinceEventJobTest {

    private lateinit var structureService: StructureService
    private lateinit var propertyService: PropertyService
    private lateinit var timeSinceEventService: TimeSinceEventService
    private val timeSinceEventConfigurationProperties = TimeSinceEventConfigurationProperties()
    private lateinit var timeSinceEventJob: TimeSinceEventJob

    @BeforeEach
    fun init() {
        structureService = mockk()
        propertyService = mockk()
        timeSinceEventService = mockk(relaxed = true)
        timeSinceEventJob = TimeSinceEventJob(
            structureService,
            propertyService,
            timeSinceEventService,
            timeSinceEventConfigurationProperties,
        )
    }

    @Test
    fun `Default job configuration`() {
        val project = Project.of(NameDescription.nd("P", ""))
        every { structureService.projectList } returns listOf(project)
        every { propertyService.hasProperty(project, BranchingModelPropertyType::class.java) } returns true

        val job = timeSinceEventJob.collectJobRegistrations().toList().firstOrNull()
        assertNotNull(job) {
            // Every 30 minutes by default
            assertEquals(30 * 60, it.schedule.period)
            assertEquals(TimeUnit.SECONDS, it.schedule.unit)
            // Key
            assertEquals("P", it.job.key.id)
            assertEquals("P time since events", it.job.description)
        }
    }

    @Test
    fun `Custom job configuration`() {
        timeSinceEventConfigurationProperties.interval = Duration.ofMinutes(120)
        val project = Project.of(NameDescription.nd("P", ""))
        every { structureService.projectList } returns listOf(project)
        every { propertyService.hasProperty(project, BranchingModelPropertyType::class.java) } returns true

        val job = timeSinceEventJob.collectJobRegistrations().toList().firstOrNull()
        assertNotNull(job) {
            // Every 120 minutes as per configuration
            assertEquals(120 * 60, it.schedule.period)
            assertEquals(TimeUnit.SECONDS, it.schedule.unit)
            // Key
            assertEquals("P", it.job.key.id)
        }
    }

    @Test
    fun `No job if no branching model property`() {
        val project = Project.of(NameDescription.nd("P", ""))
        every { structureService.projectList } returns listOf(project)
        every { propertyService.hasProperty(project, BranchingModelPropertyType::class.java) } returns false

        val job = timeSinceEventJob.collectJobRegistrations().toList().firstOrNull()
        assertNull(job)
    }

    @Test
    fun `No job if project is disabled`() {
        val project = Project.of(NameDescription.nd("P", "")).withDisabled(true)
        every { structureService.projectList } returns listOf(project)
        every { propertyService.hasProperty(project, BranchingModelPropertyType::class.java) } returns true

        val job = timeSinceEventJob.collectJobRegistrations().toList().firstOrNull()
        assertNull(job)
    }

    @Test
    fun `Running the job calls the service`() {
        val project = Project.of(NameDescription.nd("P", ""))
        every { structureService.projectList } returns listOf(project)
        every { propertyService.hasProperty(project, BranchingModelPropertyType::class.java) } returns true

        val job = timeSinceEventJob.collectJobRegistrations().toList().firstOrNull()
        assertNotNull(job) {
            it.job.task.run { progress -> println(progress) }
            verify(atLeast = 1) {
                timeSinceEventService.collectTimesSinceEvents(
                    project = project,
                    logger = any()
                )
            }
        }
    }

}