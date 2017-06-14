package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.ProjectDelete;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.ProjectRole;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RolesServiceImplTest {

    private RolesServiceImpl rolesService;

    @Before
    public void init() {
        rolesService = new RolesServiceImpl(roleContributors);
        rolesService.start();
    }

    @Test
    public void getGlobalRole_administrator() {
        assertTrue(rolesService.getGlobalRole("ADMINISTRATOR").isPresent());
    }

    @Test
    public void getGlobalRole_controller() {
        assertTrue(rolesService.getGlobalRole("CONTROLLER").isPresent());
    }

    @Test
    public void getGlobalRole_unknown() {
        assertFalse(rolesService.getGlobalRole("XXX").isPresent());
    }

    @Test
    public void getProjectRole_owner() {
        Optional<ProjectRole> owner = rolesService.getProjectRole("OWNER");
        assertTrue(owner.isPresent());
        assertTrue(owner.get().getFunctions().contains(ProjectEdit.class));
        assertFalse(owner.get().getFunctions().contains(ProjectDelete.class));
    }

}