package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*

public class ProjectControllerTest {

    public static final ID id = ID.of(1)
    private ProjectController controller;
    private StructureService structureService;

    @Before
    void before() {
        structureService = mock(StructureService)
        CopyService copyService = mock(CopyService)
        SecurityService securityService = mock(SecurityService)
        ProjectFavouriteService projectFavouriteService = mock(ProjectFavouriteService)
        controller = new ProjectController(structureService, copyService, securityService, projectFavouriteService)
    }

    @Test
    void 'Enabling a project'() {
        Project p = Project.of(NameDescription.nd("P", "Project").asState(true)).withId(id)
        assert p.disabled
        when(structureService.getProject(id)).thenReturn(p)
        p = controller.enableProject(id)
        assert !p.disabled
        verify(structureService, times(1)).saveProject(p.withDisabled(false))
    }

    @Test
    void 'Disabling a project'() {
        Project p = Project.of(NameDescription.nd("P", "Project").asState(false)).withId(id)
        assert !p.disabled
        when(structureService.getProject(id)).thenReturn(p)
        p = controller.disableProject(id)
        assert p.disabled
        verify(structureService, times(1)).saveProject(p.withDisabled(true))
    }

}