package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.job.Job
import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import java.time.LocalDateTime
import java.util.stream.Collectors

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class StaleBranchesJobTest {

    private StaleJobService staleJobService
    private StalePropertyType propertyType
    private StructureService structureService
    private PropertyService propertyService
    private EventQueryService eventQueryService
    private JobScheduler jobScheduler
    private Project project
    private Branch branch
    private LocalDateTime now
    private LocalDateTime disablingTime
    private LocalDateTime deletingTime

    @Before
    void before() {
        structureService = mock(StructureService)
        propertyService = mock(PropertyService)
        eventQueryService = mock(EventQueryService)
        jobScheduler = mock(JobScheduler)
        propertyType = new StalePropertyType(
                new StaleExtensionFeature()
        )

        staleJobService = new StaleJobServiceImpl(
                structureService,
                propertyService,
                eventQueryService
        )

        project = Project.of(nd('P', '')).withId(ID.of(1))
        branch = Branch.of(project, nd('B', '')).withId(ID.of(1))

        // Structure
        when(structureService.getProjectList()).thenReturn([project])

        // By default, no build
        when(structureService.getLastBuild(any(ID))).thenReturn(Optional.empty())

        // Times
        now = LocalDateTime.now()
        disablingTime = now.minusDays(5)
        deletingTime = now.minusDays(10)
    }

    @Test
    void 'Template branches are not managed'() {
        // Branch template
        branch = branch.withType(BranchType.TEMPLATE_DEFINITION)
        // Branch creation for deletion (normally)
        configureBranchCreationEvent(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Deleting a branch using last build time'() {
        // Last build for deletion
        configureBuild(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Disabling only a branch using last build time when deletion time is not set'() {
        // Deletion time is not set
        deletingTime = null
        // Last build for deletion
        configureBuild(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Disabling a branch using last build time'() {
        // Last build for disabling
        configureBuild(6)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Not touching a branch using last build time'() {
        // Last build still OK
        configureBuild(4)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Deleting a branch using branch creation time'() {
        // Configure branch for deletion
        configureBranchCreationEvent(11)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Disabling a branch using branch creation time'() {
        // Configure branch for disabling
        configureBranchCreationEvent(6)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Not touching a branch using branch creation time'() {
        // Configure branch for OK
        configureBranchCreationEvent(4)

        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Not touching a branch when no branch creation time'() {
        staleJobService.detectAndManageStaleBranch(branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'No scan for project without stale property'() {
        // No configuration
        configureProject(null)
        // Call
        staleJobService.detectAndManageStaleBranches({ println it } as JobRunListener, project)
        // Check
        verify(structureService, never()).getBranchesForProject(any(ID))
    }

    @Test
    void 'No scan for project with no disabling duration'() {
        // No configuration
        configureProject(StaleProperty.create())
        // Call
        staleJobService.detectAndManageStaleBranches({ println it } as JobRunListener, project)
        // Check
        verify(structureService, never()).getBranchesForProject(any(ID))
    }

    @Test
    void 'Scan for project with disabling duration'() {
        // Configuration
        configureProject(StaleProperty.create().withDisablingDuration(1))
        // Call
        staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
        // Check
        verify(structureService).getBranchesForProject(project.id)
    }

    @Test
    void 'Configured projects do schedule jobs at startup'() {
        // Configuration
        configureProject(StaleProperty.create().withDisablingDuration(1))
        // Gets the list of jobs
        List<JobRegistration> jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has not been scheduled
        assert jobs.find { it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(String.valueOf(project.id))} != null
    }

    @Test
    void 'Unconfigured projects do schedule jobs at startup'() {
        // No configuration
        configureProject(null)
        // Gets the list of jobs
        List<JobRegistration> jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has not been scheduled
        assert jobs.find { it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(String.valueOf(project.id))} == null
    }

    @Test
    void 'Configuring a project does schedule a job'() {
        // Configuration
        def property = StaleProperty.create().withDisablingDuration(1)
        configureProject(property)
        // Gets the list of jobs
        List<JobRegistration> jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has been scheduled
        assert jobs.find { it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(String.valueOf(project.id))} != null
    }

    @Test
    void 'Unconfiguring a project does unschedule a job'() {
        // Configuration
        def property = StaleProperty.create().withDisablingDuration(1)
        configureProject(property)
        // Gets the list of jobs
        List<JobRegistration> jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        // Verifies the job has been scheduled
        assert jobs.find { it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(String.valueOf(project.id))} != null
        // Unconfigures the project
        configureProject(null)
        // Verifies the job has been unscheduled
        jobs = staleJobService.collectJobRegistrations().collect(Collectors.toList())
        assert jobs.find { it.job.key == StaleJobServiceImpl.STALE_BRANCH_JOB.getKey(String.valueOf(project.id))} == null
    }

    protected def configureProject(StaleProperty property) {
        when(propertyService.getProperty(project, StalePropertyType)).thenReturn(
                Property.of(
                        propertyType,
                        property
                )
        )
        when(propertyService.hasProperty(project, StalePropertyType)).thenReturn(property != null)
    }

    protected def configureBranchCreationEvent(int branchAge) {
        when(eventQueryService.getEvents(ProjectEntityType.BRANCH, branch.id, EventFactory.NEW_BRANCH, 0, 1)).thenReturn([
                new Event(
                        EventFactory.NEW_BRANCH,
                        Signature.of(
                                now.minusDays(branchAge),
                                'test'
                        ),
                        [:],
                        null,
                        [:]
                )
        ])
    }

    protected def configureBuild(int buildAge) {
        when(structureService.getLastBuild(branch.id)).thenReturn(Optional.of(
                Build.of(
                        branch,
                        nd('1', ''),
                        Signature.of(
                                now.minusDays(buildAge),
                                'test'
                        )
                )
        ))
    }

}
