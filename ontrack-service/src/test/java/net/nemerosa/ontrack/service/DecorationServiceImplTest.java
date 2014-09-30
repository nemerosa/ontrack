package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecorationServiceImplTest {

    @Test
    public void error_decoration_generates_default_error_decoration() {
        ExtensionManager extensionManager = mock(ExtensionManager.class);
        ProjectEntity projectEntity = mock(ProjectEntity.class);

        Decorator decorator = mock(Decorator.class);
        when(decorator.getDecoration(any(ProjectEntity.class))).thenThrow(new RuntimeException("Error while generating the decoration"));

        SecurityService securityService = mock(SecurityService.class);

        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, securityService);

        Decoration decoration = service.getDecoration(projectEntity, decorator);
        assertNotNull(decoration);
        assertEquals("error", decoration.getId());
        assertEquals("Problem while getting decoration", decoration.getTitle());
    }

    @Test
    public void base_exception_decoration_generates_default_error_decoration() {
        ExtensionManager extensionManager = mock(ExtensionManager.class);
        ProjectEntity projectEntity = mock(ProjectEntity.class);

        Decorator decorator = mock(Decorator.class);
        when(decorator.getDecoration(any(ProjectEntity.class))).thenThrow(new TestBaseException());

        SecurityService securityService = mock(SecurityService.class);

        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, securityService);

        Decoration decoration = service.getDecoration(projectEntity, decorator);
        assertNotNull(decoration);
        assertEquals("error", decoration.getId());
        assertEquals("Known exception", decoration.getTitle());
    }

    public static class TestBaseException extends BaseException {

        public TestBaseException() {
            super("Known exception");
        }
    }

}
