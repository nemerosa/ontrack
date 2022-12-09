package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecorationServiceImplTest {

    @Test
    public void error_decoration_generates_default_error_decoration() {
        ProjectEntity projectEntity = mock(ProjectEntity.class);

        DecorationExtension decorator = mock(DecorationExtension.class);
        when(decorator.getDecorations(any(ProjectEntity.class))).thenThrow(new RuntimeException("Error while generating the decoration"));

        ExtensionManager extensionManager = mock(ExtensionManager.class);
        when(extensionManager.getExtensions(DecorationExtension.class)).thenReturn(
                Collections.singletonList(decorator)
        );

        SecurityService securityService = mock(SecurityService.class);

        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, securityService, mock(ApplicationLogService.class));

        @SuppressWarnings("unchecked")
        List<? extends Decoration> decorations = service.getDecorations(projectEntity, decorator);
        assertNotNull(decorations);
        assertEquals(1, decorations.size());
        Decoration decoration = decorations.get(0);
        assertNull(decoration.getData());
        assertEquals("Problem while getting decoration", decoration.getError());
    }

    @Test
    public void base_exception_decoration_generates_default_error_decoration() {
        ProjectEntity projectEntity = mock(ProjectEntity.class);
        when(projectEntity.getEntityDisplayName()).thenReturn("project");

        DecorationExtension decorator = mock(DecorationExtension.class);
        when(decorator.getDecorations(any(ProjectEntity.class))).thenThrow(new TestBaseException());

        ExtensionManager extensionManager = mock(ExtensionManager.class);
        when(extensionManager.getExtensions(DecorationExtension.class)).thenReturn(
                Collections.singletonList(decorator)
        );

        SecurityService securityService = mock(SecurityService.class);

        DecorationServiceImpl service = new DecorationServiceImpl(extensionManager, securityService, mock(ApplicationLogService.class));

        @SuppressWarnings("unchecked")
        List<? extends Decoration> decorations = service.getDecorations(projectEntity, decorator);
        assertNotNull(decorations);
        assertEquals(1, decorations.size());
        Decoration decoration = decorations.get(0);
        assertEquals("Known exception", decoration.getError());
        assertNull(decoration.getData());
    }

    public static class TestBaseException extends BaseException {

        public TestBaseException() {
            super("Known exception");
        }
    }

}
