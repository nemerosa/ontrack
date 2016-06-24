package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityServiceUnitTest {

    private SecurityServiceImpl securityService;
    private CachedSettingsService cachedSettingsService;

    @Before
    public void before() {
        cachedSettingsService = mock(CachedSettingsService.class);
        securityService = new SecurityServiceImpl(cachedSettingsService);
    }

    @Test
    public void is_global_grant_project_view_granted_to_none() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of().withGrantProjectViewToAll(false)
        );
        assertFalse("No global grant for the project view", securityService.isGlobalGrant(ProjectView.class));
    }

    @Test
    public void is_global_grant_project_view_granted_to_all_for_project_view() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of().withGrantProjectViewToAll(true)
        );
        assertTrue("Global grant for the project view", securityService.isGlobalGrant(ProjectView.class));
    }

    @Test
    public void is_global_grant_project_view_granted_to_all_does_not_work_for_project_edit() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of().withGrantProjectViewToAll(true)
        );
        assertFalse(
                "Global grant for the project view does not grant the project edit function",
                securityService.isGlobalGrant(ProjectEdit.class)
        );
    }

    @Test
    public void is_global_grant_project_view_granted_to_all_does_not_work_for_project_config() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of().withGrantProjectViewToAll(true)
        );
        assertFalse(
                "Global grant for the project view does not grant the project config function",
                securityService.isGlobalGrant(ProjectConfig.class)
        );
    }

    @Test
    public void project_view_granted_for_anonymous_by_default() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of()
        );
        assertTrue(
                "Project view is granted by default for anonymous users",
                securityService.isProjectFunctionGranted(1, ProjectView.class)
        );
    }

    @Test
    public void project_view_granted_when_settings_ok() {
        when(cachedSettingsService.getCachedSettings(SecuritySettings.class)).thenReturn(
                SecuritySettings.of().withGrantProjectViewToAll(true)
        );
        assertTrue(
                "Project view is granted for anonymous users when set explicitely",
                securityService.isProjectFunctionGranted(1, ProjectView.class)
        );
    }

    protected boolean protectedCall() {
        securityService.checkGlobalFunction(ProjectCreation.class);
        return true;
    }

    @Test(expected = AccessDeniedException.class)
    public void run_as_admin_not_applied() {
        protectedCall();
    }

    @Test
    public void run_as_admin() {
        assertTrue(securityService.runAsAdmin(this::protectedCall).get());
    }

}
