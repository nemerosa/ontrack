package net.nemerosa.ontrack.extension.stale

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.Before
import org.junit.Test

import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StaleBranchesJobTest {

    private lateinit var staleJobService: StaleJobService
    private lateinit var propertyType: StalePropertyType
    private lateinit var structureService: StructureService
    private lateinit var propertyService: PropertyService
    private lateinit var eventQueryService: EventQueryService
    private lateinit var jobScheduler: JobScheduler
    private lateinit var project: Project
    private lateinit var branch: Branch
    private lateinit var now: LocalDateTime
    private lateinit var disablingTime: LocalDateTime
    private var deletingTime: LocalDateTime? = null

    @Before
    fun before() {
        structureService = mock()
        propertyService = mock()
        eventQueryService = mock()
        jobScheduler = mock()
        propertyType = StalePropertyType(StaleExtensionFeature())

        staleJobService = StaleJobServiceImpl(structureService, propertyService)

        project = Project.of(nd("P", "")).withId(ID.of(1))
        branch = Branch.of(project, nd("B", "")).withId(ID.of(1))

        // Structure
        whenever(structureService.projectList).thenReturn(listOf(project))

        // By default, no build
        whenever(structureService.getLastBuild(any())).thenReturn(Optional.empty())

        // Last promotions
        whenever(structureService.getBranchStatusView(branch)).thenReturn(
                BranchStatusView(
                        branch,
                        emptyList(),
                        null,
                        emptyList()
                )
        )

        // Times
        now = LocalDateTime.now()
        disablingTime = now.minusDays(5)
        deletingTime = now.minusDays(10)
    }

    @Test
    fun `Template branches are not managed`() {
        // Branch template
        branch = branch.withType(BranchType.TEMPLATE_DEFINITION)
        // Branch creation for deletion (normally)
        configureBranchCreationEvent(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Deleting a branch using last build time`() {
        // Last build for deletion
        configureBuild(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Disabling only a branch using last build time when deletion time is not set`() {
        // Deletion time is not set
        deletingTime = null
        // Last build for deletion
        configureBuild(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    fun `Disabling a branch using last build time`() {
        // Last build for disabling
        configureBuild(6)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    fun `Not touching a branch using last build time`() {
        // Last build still OK
        configureBuild(4)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Deleting a branch using branch creation time`() {
        // Configure branch for deletion
        configureBranchCreationEvent(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Disabling a branch using branch creation time`() {
        // Configure branch for disabling
        configureBranchCreationEvent(6)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    fun `Not disabling a branch because of promotions`() {
        // Configure branch for disabling
        configureBranchCreationEvent(6)
        // Promotion
        configureBranchForPromotion()

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, listOf("PRODUCTION"))

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Disabling a branch even if promoted when not configured`() {
        // Configure branch for disabling
        configureBranchCreationEvent(6)
        // Promotion
        configureBranchForPromotion()

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    private fun configureBranchForPromotion() {
        val production = PromotionLevel.of(branch, nd("PRODUCTION", ""))
        whenever(structureService.getBranchStatusView(branch)).thenReturn(
                BranchStatusView(
                        branch,
                        emptyList(),
                        null,
                        listOf(
                                PromotionView(
                                        production,
                                        PromotionRun.of(
                                                Build.of(branch, nd("1", ""), Signature.of("test")),
                                                production,
                                                Signature.of("test"),
                                                ""
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun `Not deleting a branch because of promotions`() {
        // Configure branch for deletion
        configureBranchCreationEvent(11)
        // Promotion
        configureBranchForPromotion()

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, listOf("PRODUCTION"))

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Deleting a branch even if promoted when not configured`() {
        // Configure branch for deletion
        configureBranchCreationEvent(11)
        // Promotion
        configureBranchForPromotion()

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Not touching a branch using branch creation time`() {
        // Configure branch for OK
        configureBranchCreationEvent(4)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `Not touching a branch when no branch creation time`() {
        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime, emptyList())

        verify(structureService, never()).deleteBranch(any())
        verify(structureService, never()).saveBranch(any())
    }

    @Test
    fun `No scan for project without stale property`() {
        // No configuration
        configureProject(null)
        // Call
        staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
        // Check
        verify(structureService, never()).getBranchesForProject(any())
    }

    @Test
    fun `No scan for project with no disabling duration`() {
        // No configuration
        configureProject(StaleProperty.create())
        // Call
        staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
        // Check
        verify(structureService, never()).getBranchesForProject(any())
    }

    @Test
    fun `Scan for project with disabling duration`() {
        // Configuration
        configureProject(StaleProperty.create().withDisablingDuration(1))
        // Call
        staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
        // Check
        verify(structureService).getBranchesForProject(project.id)
    }

    @Test
    fun `Configured projects do schedule jobs at startup`() {
        // Configuration
        configureProject(StaleProperty.create().withDisablingDuration(1))
        // Gets the list of jobs
        val jobs: List<JobRegistration> = staleJobService.collectJobRegistrations().toList()
        // Verifies the job has not been scheduled
        val job = jobs.find {
            it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(project.id.toString())
        }
        assertNotNull(job)
    }

    @Test
    fun `Unconfigured projects do schedule jobs at startup`() {
        // No configuration
        configureProject(null)
        // Gets the list of jobs
        val jobs = staleJobService.collectJobRegistrations().toList()
        // Verifies the job has not been scheduled
        val job = jobs.find {
            it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(project.id.toString())
        }
        assertNull(job)
    }

    @Test
    fun `Configuring a project does schedule a job`() {
        // Configuration
        val property = StaleProperty.create().withDisablingDuration(1)
        configureProject(property)
        // Gets the list of jobs
        val jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has been scheduled
        val job = jobs.find {
            it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(project.id.toString())
        }
        assertNotNull(job)
    }

    @Test
    fun `Unconfiguring a project does unschedule a job`() {
        // Configuration
        val property = StaleProperty.create().withDisablingDuration(1)
        configureProject(property)
        // Gets the list of jobs
        val jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has been scheduled
        assertNotNull(jobs.find {
            it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(project.id.toString())
        })
        // Unconfigures the project
        configureProject(null)
        // Verifies the job has been unscheduled
        val newJobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        assertNull(newJobs.find {
            it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(project.id.toString())
        })
    }

    private fun configureProject(property: StaleProperty?) {
        whenever(propertyService.getProperty(project, StalePropertyType::class.java)).thenReturn(
                Property.of(
                        propertyType,
                        property
                )
        )
        whenever(propertyService.hasProperty(project, StalePropertyType::class.java)).thenReturn(property != null)
    }

    private fun configureBranchCreationEvent(branchAge: Int) {
        branch = branch.withSignature(
                Signature.of(
                        now.minusDays(branchAge.toLong()),
                        "test"
                )
        )
        // Last promotions
        whenever(structureService.getBranchStatusView(branch)).thenReturn(
                BranchStatusView(
                        branch,
                        emptyList(),
                        null,
                        emptyList()
                )
        )
    }

    private fun configureBuild(buildAge: Int) {
        whenever(structureService.getLastBuild(branch.id)).thenReturn(Optional.of(
                Build.of(
                        branch,
                        nd("1", ""),
                        Signature.of(
                                now.minusDays(buildAge.toLong()),
                                "test"
                        )
                )
        ))
        whenever(structureService.getBranchStatusView(branch)).thenReturn(
                BranchStatusView(
                        branch,
                        emptyList(),
                        null,
                        emptyList()
                )
        )
    }

}
