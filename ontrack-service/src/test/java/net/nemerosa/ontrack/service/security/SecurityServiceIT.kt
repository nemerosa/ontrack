package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Project;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

public class SecurityServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private SecurityService securityService;

    @Test
    public void getCurrentAccount() throws Exception {
        OntrackAuthenticatedUser account = asUser().call(securityService::getCurrentAccount);
        assertNotNull(account);
    }

    @Test
    public void getCurrentAccount_none() throws Exception {
        OntrackAuthenticatedUser account = securityService.getCurrentAccount();
        assertNull(account);
    }

    @Test
    public void runner_function() throws Exception {
        // Function that needs an authentication context
        Function<String, String> fn = (s) -> s + " -> " + getContextName();
        // Testing outside a context
        assertEquals("test -> none", fn.apply("test"));
        // With a context
        Function<String, String> securedFn = asUser().with(ProjectCreation.class).call(() -> securityService.runner(fn));
        // Calls the secured function
        assertEquals("test -> TestingAuthenticationToken", securedFn.apply("test"));
    }

    private static String getContextName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getClass().getSimpleName() : "none";
    }

    @Test
    public void read_only_on_one_project() throws Exception {
        withNoGrantViewToAll(() -> {
            // Creates two projects
            Project p1 = doCreateProject();
            Project p2 = doCreateProject();
            // Creates an account authorised to access only one project
            Account account = doCreateAccountWithProjectRole(p2, "READ_ONLY");
            return asAccount(account).call(() -> {
                // With this account, gets the list of projects
                List<Project> list = structureService.getProjectList();
                // Checks we only have one project
                assertEquals(1, list.size());
                assertEquals(p2.getName(), list.get(0).getName());
                // Access to the authorised project
                assertTrue(structureService.findProjectByName(p2.getName()).isPresent());
                assertNotNull(structureService.getProject(p2.getId()));
                // No access to the other project
                assertFalse(structureService.findProjectByName(p1.getName()).isPresent());
                try {
                    structureService.getProject(p1.getId());
                    fail("Project is not authorised");
                } catch (AccessDeniedException ignored) {
                    assertTrue("Project cannot be found", true);
                }
                // OK
                return true;
            });
        });
    }

    @Test
    public void read_only_on_all_projects() throws Exception {
        withNoGrantViewToAll(() -> {
            // Creates two projects
            Project p1 = doCreateProject();
            Project p2 = doCreateProject();
            // Creates an account authorised to access all projects
            Account account = doCreateAccountWithGlobalRole("READ_ONLY");
            return asAccount(account).call(() -> {
                // With this account, gets the list of projects
                List<Project> list = structureService.getProjectList();
                // Checks we only have the two projects (among all others)
                assertTrue(list.size() >= 2);
                assertTrue(list.stream().anyMatch(project -> StringUtils.equals(p1.getName(), project.getName())));
                assertTrue(list.stream().anyMatch(project -> StringUtils.equals(p2.getName(), project.getName())));
                // Access to the projects
                assertTrue(structureService.findProjectByName(p1.getName()).isPresent());
                assertNotNull(structureService.getProject(p1.getId()));
                assertTrue(structureService.findProjectByName(p2.getName()).isPresent());
                assertNotNull(structureService.getProject(p2.getId()));
                // OK
                return true;
            });
        });
    }

}
