package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

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
        when(decorator.getDecorations(any(ProjectEntity.class))).thenThrow(new RuntimeException("Error while generating the decoration"));

        SecurityService securityService = mock(SecurityService.class);

        List<Decorator> builtinDecorators = Collections.emptyList();
        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, builtinDecorators, securityService);

        @SuppressWarnings("unchecked")
        List<? extends Decoration> decorations = service.getDecorations(projectEntity, decorator);
        assertNotNull(decorations);
        assertEquals(1, decorations.size());
        Decoration decoration = decorations.get(0);
        assertEquals("Problem while getting decoration", decoration.getData());
    }

    @Test
    public void base_exception_decoration_generates_default_error_decoration() {
        ExtensionManager extensionManager = mock(ExtensionManager.class);
        ProjectEntity projectEntity = mock(ProjectEntity.class);

        Decorator decorator = mock(Decorator.class);
        when(decorator.getDecorations(any(ProjectEntity.class))).thenThrow(new TestBaseException());

        SecurityService securityService = mock(SecurityService.class);

        List<Decorator> builtinDecorators = Collections.emptyList();
        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, builtinDecorators, securityService);

        @SuppressWarnings("unchecked")
        List<? extends Decoration> decorations = service.getDecorations(projectEntity, decorator);
        assertNotNull(decorations);
        assertEquals(1, decorations.size());
        Decoration decoration = decorations.get(0);
        assertEquals("Known exception", decoration.getData());
    }

    public static class TestBaseException extends BaseException {

        public TestBaseException() {
            super("Known exception");
        }
    }

}
