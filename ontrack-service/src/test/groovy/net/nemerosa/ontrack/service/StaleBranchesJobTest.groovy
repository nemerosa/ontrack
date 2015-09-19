package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.buildfilter.BuildFilter
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.GeneralSettings
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import java.time.LocalDateTime

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class StaleBranchesJobTest {

    private StaleBranchesJob job
    private StructureService structureService
    private EventQueryService eventQueryService
    private CachedSettingsService cachedSettingsService
    private Project project
    private Branch branch
    private LocalDateTime now
    private LocalDateTime disablingTime
    private Optional<LocalDateTime> deletingTime

    @Before
    void before() {
        structureService = mock(StructureService)
        def propertyService = mock(PropertyService)
        eventQueryService = mock(EventQueryService)
        cachedSettingsService = mock(CachedSettingsService)
        job = new StaleBranchesJob(
                structureService,
                propertyService,
                eventQueryService,
                cachedSettingsService
        )


        project = Project.of(nd('P', '')).withId(ID.of(1))
        branch = Branch.of(project, nd('B', '')).withId(ID.of(1))

        // By default, no build
        when(structureService.getFilteredBuilds(any(ID), any(BuildFilter))).thenReturn([])

        // Times
        now = LocalDateTime.now()
        disablingTime = now.minusDays(5)
        deletingTime = Optional.of(now.minusDays(10))
    }

    @Test
    void 'Template branches are not managed'() {
        // Branch template
        branch = branch.withType(BranchType.TEMPLATE_DEFINITION)
        // Branch creation for deletion (normally)
        configureBranchCreationEvent(11)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Deleting a branch using last build time'() {
        // Last build for deletion
        configureBuild(11)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Disabling only a branch using last build time when deletion time is not set'() {
        // Deletion time is not set
        deletingTime = Optional.empty()
        // Last build for deletion
        configureBuild(11)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Disabling a branch using last build time'() {
        // Last build for disabling
        configureBuild(6)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Not touching a branch using last build time'() {
        // Last build still OK
        configureBuild(4)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Deleting a branch using branch creation time'() {
        // Configure branch for deletion
        configureBranchCreationEvent(11)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService).deleteBranch(branch.id)
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Disabling a branch using branch creation time'() {
        // Configure branch for disabling
        configureBranchCreationEvent(6)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService).saveBranch(branch.withDisabled(true))
    }

    @Test
    void 'Not touching a branch using branch creation time'() {
        // Configure branch for OK
        configureBranchCreationEvent(4)

        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'Not touching a branch when no branch creation time'() {
        job.detectAndManageStaleBranch({ println it }, branch, disablingTime, deletingTime)

        verify(structureService, never()).deleteBranch(any(ID))
        verify(structureService, never()).saveBranch(any(Branch))
    }

    @Test
    void 'No scan when disabling is set to 0'() {
        // Settings
        when(cachedSettingsService.getCachedSettings(GeneralSettings)).thenReturn(
                GeneralSettings.of() // Defaults
        )
        // Scan
        job.detectAndManageStaleBranches({println it})
        // No scan
        verify(structureService, never()).getProjectList()
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
        when(structureService.getFilteredBuilds(eq(branch.id), any(BuildFilter))).thenReturn([
                Build.of(
                        branch,
                        nd('1', ''),
                        Signature.of(
                                now.minusDays(buildAge),
                                'test'
                        )
                )
        ])
    }

}
