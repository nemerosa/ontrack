package net.nemerosa.ontrack.service.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RolesServiceImplTest {

    private RolesServiceImpl rolesService;

    @Before
    public void init() {
        rolesService = new RolesServiceImpl();
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

}